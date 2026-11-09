package com.showticketing.pdv.service;

import com.showticketing.pdv.dto.*;
import com.showticketing.pdv.entity.*;
import com.showticketing.pdv.enums.PaymentMethod;
import com.showticketing.pdv.enums.TicketStatus;
import com.showticketing.pdv.enums.TicketType;
import com.showticketing.pdv.exception.BusinessException;
import com.showticketing.pdv.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SaleService {

    private final CashSessionService cashSessionService;
    private final EventRepository eventRepository;
    private final SectorRepository sectorRepository;
    private final BatchRepository batchRepository;
    private final SaleOrderRepository saleOrderRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;

    public SaleService(CashSessionService cashSessionService,
                       EventRepository eventRepository,
                       SectorRepository sectorRepository,
                       BatchRepository batchRepository,
                       SaleOrderRepository saleOrderRepository,
                       TicketRepository ticketRepository,
                       QrCodeService qrCodeService) {
        this.cashSessionService = cashSessionService;
        this.eventRepository = eventRepository;
        this.sectorRepository = sectorRepository;
        this.batchRepository = batchRepository;
        this.saleOrderRepository = saleOrderRepository;
        this.ticketRepository = ticketRepository;
        this.qrCodeService = qrCodeService;
    }

    @Transactional
    public SaleResponse processSale(SaleRequest req) {
        CashSession session = cashSessionService.getCurrentActiveSession();

        SaleOrder order = new SaleOrder();
        String orderCode = "PDV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + "-" +
                String.format("%04d", (int)(Math.random() * 9000 + 1000));
        order.setOrderCode(orderCode);
        order.setCashSession(session);
        order.setPosTerminalId(session.getPosTerminalId());
        order.setOperatorName(session.getOperatorName());
        order.setCustomerName(req.getCustomerName());
        order.setCustomerCpf(req.getCustomerCpf());
        order.setCustomerEmail(req.getCustomerEmail());
        order.setCreatedAt(LocalDateTime.now());
        order.setDiscount(req.getDiscount() != null ? req.getDiscount() : BigDecimal.ZERO);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<Ticket> generatedTickets = new ArrayList<>();

        for (SaleItemRequest itemReq : req.getItems()) {
            Event event = eventRepository.findByEventId(itemReq.getEventId())
                    .orElseThrow(() -> new BusinessException("Evento não encontrado: " + itemReq.getEventId()));
            Sector sector = sectorRepository.findBySectorId(itemReq.getSectorId())
                    .orElseThrow(() -> new BusinessException("Setor não encontrado: " + itemReq.getSectorId()));
            Batch batch = batchRepository.findByBatchId(itemReq.getBatchId())
                    .orElseThrow(() -> new BusinessException("Lote não encontrado: " + itemReq.getBatchId()));

            if (sector.getAvailableQuantity() < itemReq.getQuantity()) {
                throw new BusinessException("Capacidade esgotada no setor " + sector.getName());
            }
            if (batch.getAvailableQuantity() < itemReq.getQuantity()) {
                throw new BusinessException("Ingressos insuficientes no lote " + batch.getBatchNumber() + " de " + sector.getName());
            }

            // Decrementa estoque
            sector.setAvailableQuantity(sector.getAvailableQuantity() - itemReq.getQuantity());
            batch.setSoldQuantity(batch.getSoldQuantity() + itemReq.getQuantity());

            sectorRepository.save(sector);
            batchRepository.save(batch);

            TicketType ticketType = itemReq.getTicketType();
            BigDecimal basePrice = batch.getBasePrice();
            BigDecimal multiplier = ticketType.getMultiplier();
            BigDecimal unitPrice = basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
            BigDecimal discountPercentage = BigDecimal.valueOf(100).subtract(multiplier.multiply(BigDecimal.valueOf(100)));
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            subtotal = subtotal.add(itemSubtotal);

            SaleOrderItem orderItem = new SaleOrderItem();
            orderItem.setEvent(event);
            orderItem.setSector(sector);
            orderItem.setBatch(batch);
            orderItem.setTicketType(ticketType);
            orderItem.setOriginalPrice(basePrice);
            orderItem.setDiscountPercentage(discountPercentage);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setSubtotal(itemSubtotal);
            orderItem.setHolderName(itemReq.getHolderName() != null ? itemReq.getHolderName() : req.getCustomerName());
            orderItem.setDocumentNumber(itemReq.getDocumentNumber());
            order.addItem(orderItem);

            // Gera cada ingresso individual
            for (int i = 0; i < itemReq.getQuantity(); i++) {
                Ticket ticket = new Ticket();
                String ticketUuid = UUID.randomUUID().toString();
                String ticketCode = "TKT-" + event.getEventId().toUpperCase().replace("EVT_", "") + "-" +
                        ticketUuid.substring(0, 8).toUpperCase();

                ticket.setTicketId(ticketUuid);
                ticket.setTicketCode(ticketCode);
                ticket.setEvent(event);
                ticket.setSector(sector);
                ticket.setBatch(batch);
                ticket.setTicketType(ticketType);
                ticket.setPricePaid(unitPrice);
                ticket.setHolderName(orderItem.getHolderName());
                ticket.setHolderCpf(req.getCustomerCpf());
                ticket.setDocumentNumber(orderItem.getDocumentNumber());
                ticket.setIssuedAt(LocalDateTime.now());
                ticket.setStatus(TicketStatus.EMITTED);

                String hash = generateValidationHash(ticketUuid + ":" + ticketCode + ":" + event.getEventId());
                ticket.setValidationHash(hash);
                ticket.setQrCodeData("SHOWTICKET:" + ticketCode + ":" + hash);

                order.addTicket(ticket);
                generatedTickets.add(ticket);
            }
        }

        order.setSubtotal(subtotal);
        BigDecimal total = subtotal.subtract(order.getDiscount()).max(BigDecimal.ZERO);
        order.setTotalAmount(total);

        // Processa Pagamentos
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalChange = BigDecimal.ZERO;

        for (PaymentRequest payReq : req.getPayments()) {
            Payment payment = new Payment();
            payment.setMethod(payReq.getMethod());
            payment.setAmountTendered(payReq.getAmountTendered());
            payment.setAmountApplied(payReq.getAmountApplied());

            if (payReq.getMethod() == PaymentMethod.CASH && payReq.getAmountTendered().compareTo(payReq.getAmountApplied()) > 0) {
                BigDecimal change = payReq.getAmountTendered().subtract(payReq.getAmountApplied());
                payment.setChangeReturned(change);
                totalChange = totalChange.add(change);
            } else {
                payment.setChangeReturned(BigDecimal.ZERO);
            }

            payment.setPixTransactionId(payReq.getPixTransactionId());
            order.addPayment(payment);
            totalPaid = totalPaid.add(payReq.getAmountApplied());
        }

        if (totalPaid.compareTo(total) < 0) {
            throw new BusinessException("Valor pago (R$ " + totalPaid + ") é menor que o valor total da venda (R$ " + total + ")");
        }

        SaleOrder savedOrder = saleOrderRepository.save(order);

        // Monta DTO de resposta
        SaleResponse resp = new SaleResponse();
        resp.setOrderCode(savedOrder.getOrderCode());
        resp.setPosTerminalId(savedOrder.getPosTerminalId());
        resp.setOperatorName(savedOrder.getOperatorName());
        resp.setCustomerName(savedOrder.getCustomerName());
        resp.setCreatedAt(savedOrder.getCreatedAt());
        resp.setSubtotal(savedOrder.getSubtotal());
        resp.setDiscount(savedOrder.getDiscount());
        resp.setTotalAmount(savedOrder.getTotalAmount());
        resp.setChangeReturned(totalChange);

        List<TicketResponse> ticketResponses = new ArrayList<>();
        for (Ticket t : generatedTickets) {
            TicketResponse tr = new TicketResponse();
            tr.setTicketId(t.getTicketId());
            tr.setTicketCode(t.getTicketCode());
            tr.setEventName(t.getEvent().getName());
            tr.setVenueName(t.getEvent().getVenueName() + " - " + t.getEvent().getCity() + "/" + t.getEvent().getState());
            tr.setEventDate(t.getEvent().getDateTime());
            tr.setSectorName(t.getSector().getName());
            tr.setBatchNumber(t.getBatch().getBatchNumber());
            tr.setTicketType(t.getTicketType());
            tr.setPricePaid(t.getPricePaid());
            tr.setHolderName(t.getHolderName());
            tr.setHolderCpf(t.getHolderCpf());
            tr.setDocumentNumber(t.getDocumentNumber());
            tr.setValidationHash(t.getValidationHash());
            tr.setQrCodeData(t.getQrCodeData());
            tr.setQrCodeBase64(qrCodeService.generateQrCodeBase64(t.getQrCodeData(), 200, 200));
            tr.setStatus(t.getStatus());
            ticketResponses.add(tr);
        }
        resp.setTickets(ticketResponses);

        return resp;
    }

    private String generateValidationHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.substring(0, 16);
        } catch (Exception e) {
            return UUID.randomUUID().toString().substring(0, 16);
        }
    }
}
