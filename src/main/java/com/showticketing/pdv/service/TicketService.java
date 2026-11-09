package com.showticketing.pdv.service;

import com.showticketing.pdv.dto.CheckInRequest;
import com.showticketing.pdv.dto.CheckInResponse;
import com.showticketing.pdv.dto.TicketResponse;
import com.showticketing.pdv.entity.Ticket;
import com.showticketing.pdv.enums.TicketStatus;
import com.showticketing.pdv.exception.BusinessException;
import com.showticketing.pdv.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;

    public TicketService(TicketRepository ticketRepository, QrCodeService qrCodeService) {
        this.ticketRepository = ticketRepository;
        this.qrCodeService = qrCodeService;
    }

    @Transactional
    public CheckInResponse validateCheckIn(CheckInRequest req) {
        String identifier = req.getTicketIdentifier().trim();

        // Se veio o payload completo do QR Code "SHOWTICKET:TKT-XXX:HASH"
        if (identifier.startsWith("SHOWTICKET:")) {
            String[] parts = identifier.split(":");
            if (parts.length >= 2) {
                identifier = parts[1];
            }
        }

        Optional<Ticket> ticketOpt = ticketRepository.findByTicketCode(identifier);
        if (ticketOpt.isEmpty()) {
            ticketOpt = ticketRepository.findByTicketId(identifier);
        }

        if (ticketOpt.isEmpty()) {
            return CheckInResponse.fail("Ingresso não encontrado ou inválido no sistema", identifier, null);
        }

        Ticket ticket = ticketOpt.get();

        if (ticket.getStatus() == TicketStatus.CHECKED_IN) {
            String timeStr = ticket.getCheckedInAt() != null ?
                    ticket.getCheckedInAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "horário não registrado";
            return CheckInResponse.fail(
                    "ATENÇÃO: Ingresso JÁ UTILIZADO em " + timeStr + (ticket.getTurnstileGate() != null ? " no " + ticket.getTurnstileGate() : ""),
                    ticket.getTicketCode(),
                    TicketStatus.CHECKED_IN
            );
        }

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            return CheckInResponse.fail("Ingresso CANCELADO. Entrada não autorizada.", ticket.getTicketCode(), TicketStatus.CANCELLED);
        }

        // Valida e dá baixa no ingresso
        ticket.setStatus(TicketStatus.CHECKED_IN);
        ticket.setCheckedInAt(LocalDateTime.now());
        ticket.setTurnstileGate(req.getGate() != null ? req.getGate() : "CATRACA-PRINCIPAL");
        ticket.setValidatedBy(req.getOperator() != null ? req.getOperator() : "OPERADOR-PORTARIA");
        ticketRepository.save(ticket);

        return CheckInResponse.ok(
                "ACESSO LIBERADO! Bom show!",
                ticket.getTicketCode(),
                ticket.getEvent().getName(),
                ticket.getSector().getName(),
                ticket.getTicketType(),
                ticket.getHolderName(),
                ticket.getCheckedInAt()
        );
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketDetails(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new BusinessException("Ingresso não encontrado: " + ticketCode));

        TicketResponse tr = new TicketResponse();
        tr.setTicketId(ticket.getTicketId());
        tr.setTicketCode(ticket.getTicketCode());
        tr.setEventName(ticket.getEvent().getName());
        tr.setVenueName(ticket.getEvent().getVenueName());
        tr.setEventDate(ticket.getEvent().getDateTime());
        tr.setSectorName(ticket.getSector().getName());
        tr.setBatchNumber(ticket.getBatch().getBatchNumber());
        tr.setTicketType(ticket.getTicketType());
        tr.setPricePaid(ticket.getPricePaid());
        tr.setHolderName(ticket.getHolderName());
        tr.setHolderCpf(ticket.getHolderCpf());
        tr.setDocumentNumber(ticket.getDocumentNumber());
        tr.setValidationHash(ticket.getValidationHash());
        tr.setQrCodeData(ticket.getQrCodeData());
        tr.setQrCodeBase64(qrCodeService.generateQrCodeBase64(ticket.getQrCodeData(), 200, 200));
        tr.setStatus(ticket.getStatus());
        return tr;
    }
}
