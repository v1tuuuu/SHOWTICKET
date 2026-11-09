package com.showticketing.pdv.service;

import com.showticketing.pdv.dto.*;
import com.showticketing.pdv.entity.*;
import com.showticketing.pdv.enums.CashSessionStatus;
import com.showticketing.pdv.enums.MovementType;
import com.showticketing.pdv.enums.PaymentMethod;
import com.showticketing.pdv.exception.BusinessException;
import com.showticketing.pdv.repository.CashMovementRepository;
import com.showticketing.pdv.repository.CashSessionRepository;
import com.showticketing.pdv.repository.SaleOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class CashSessionService {

    private final CashSessionRepository cashSessionRepository;
    private final CashMovementRepository cashMovementRepository;
    private final SaleOrderRepository saleOrderRepository;

    public CashSessionService(CashSessionRepository cashSessionRepository,
                              CashMovementRepository cashMovementRepository,
                              SaleOrderRepository saleOrderRepository) {
        this.cashSessionRepository = cashSessionRepository;
        this.cashMovementRepository = cashMovementRepository;
        this.saleOrderRepository = saleOrderRepository;
    }

    @Transactional
    public CashSession openSession(CashSessionOpenRequest req) {
        Optional<CashSession> openSession = cashSessionRepository.findFirstByStatusOrderByOpenedAtDesc(CashSessionStatus.OPEN);
        if (openSession.isPresent()) {
            throw new BusinessException("Já existe um caixa aberto para o operador " + openSession.get().getOperatorName());
        }

        String sessionId = "SESS-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        CashSession session = new CashSession(
                sessionId,
                req.getPosTerminalId(),
                req.getOperatorId(),
                req.getOperatorName(),
                req.getInitialFloat()
        );
        return cashSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public CashSession getCurrentActiveSession() {
        return cashSessionRepository.findFirstByStatusOrderByOpenedAtDesc(CashSessionStatus.OPEN)
                .orElseThrow(() -> new BusinessException("Nenhum caixa está aberto no momento. Abra o caixa antes de operar."));
    }

    @Transactional(readOnly = true)
    public Optional<CashSession> findCurrentActiveSessionOptional() {
        return cashSessionRepository.findFirstByStatusOrderByOpenedAtDesc(CashSessionStatus.OPEN);
    }

    @Transactional
    public CashMovement addMovement(String sessionId, CashMovementRequest req) {
        CashSession session = cashSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("Sessão de caixa não encontrada: " + sessionId));

        if (session.getStatus() != CashSessionStatus.OPEN) {
            throw new BusinessException("Não é possível registrar movimentação em um caixa fechado.");
        }

        CashMovement movement = new CashMovement(req.getType(), req.getAmount(), req.getReason());
        movement.setCashSession(session);
        return cashMovementRepository.save(movement);
    }

    @Transactional
    public CashSessionSummaryResponse closeSession(String sessionId, CashSessionCloseRequest req) {
        CashSession session = cashSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("Sessão de caixa não encontrada: " + sessionId));

        if (session.getStatus() != CashSessionStatus.OPEN) {
            throw new BusinessException("O caixa já se encontra fechado.");
        }

        session.setClosedAt(LocalDateTime.now());
        session.setStatus(CashSessionStatus.CLOSED);
        session.setClosingCashReported(req.getClosingCashReported());
        session.setClosingNotes(req.getClosingNotes());
        cashSessionRepository.save(session);

        return getSessionSummary(sessionId);
    }

    @Transactional(readOnly = true)
    public CashSessionSummaryResponse getSessionSummary(String sessionId) {
        CashSession session = cashSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("Sessão de caixa não encontrada: " + sessionId));

        List<SaleOrder> sales = saleOrderRepository.findByCashSessionOrderByCreatedAtDesc(session);
        List<CashMovement> movements = cashMovementRepository.findByCashSessionOrderByTimestampDesc(session);

        BigDecimal cashSales = BigDecimal.ZERO;
        BigDecimal debitSales = BigDecimal.ZERO;
        BigDecimal creditSales = BigDecimal.ZERO;
        BigDecimal pixSales = BigDecimal.ZERO;
        BigDecimal grossTotal = BigDecimal.ZERO;
        int ticketCount = 0;

        for (SaleOrder order : sales) {
            grossTotal = grossTotal.add(order.getTotalAmount());
            ticketCount += order.getItems().stream().mapToInt(SaleOrderItem::getQuantity).sum();

            for (Payment p : order.getPayments()) {
                if (p.getMethod() == PaymentMethod.CASH) {
                    cashSales = cashSales.add(p.getAmountApplied());
                } else if (p.getMethod() == PaymentMethod.DEBIT_CARD) {
                    debitSales = debitSales.add(p.getAmountApplied());
                } else if (p.getMethod() == PaymentMethod.CREDIT_CARD) {
                    creditSales = creditSales.add(p.getAmountApplied());
                } else if (p.getMethod() == PaymentMethod.PIX) {
                    pixSales = pixSales.add(p.getAmountApplied());
                }
            }
        }

        BigDecimal supplies = BigDecimal.ZERO;
        BigDecimal bleeds = BigDecimal.ZERO;
        for (CashMovement m : movements) {
            if (m.getType() == MovementType.SUPRIMENTO) {
                supplies = supplies.add(m.getAmount());
            } else if (m.getType() == MovementType.SANGRIA) {
                bleeds = bleeds.add(m.getAmount());
            }
        }

        BigDecimal expectedCash = session.getInitialFloat().add(cashSales).add(supplies).subtract(bleeds);
        BigDecimal reportedCash = session.getClosingCashReported() != null ? session.getClosingCashReported() : BigDecimal.ZERO;
        BigDecimal diff = reportedCash.subtract(expectedCash);

        CashSessionSummaryResponse response = new CashSessionSummaryResponse();
        response.setSessionId(session.getSessionId());
        response.setPosTerminalId(session.getPosTerminalId());
        response.setOperatorId(session.getOperatorId());
        response.setOperatorName(session.getOperatorName());
        response.setOpenedAt(session.getOpenedAt());
        response.setClosedAt(session.getClosedAt());
        response.setStatus(session.getStatus());

        response.setInitialFloat(session.getInitialFloat());
        response.setCashSalesTotal(cashSales);
        response.setDebitSalesTotal(debitSales);
        response.setCreditSalesTotal(creditSales);
        response.setPixSalesTotal(pixSales);
        response.setSuppliesTotal(supplies);
        response.setBleedTotal(bleeds);

        response.setExpectedCashBalance(expectedCash);
        response.setReportedCashBalance(reportedCash);
        response.setCashDifference(diff);

        response.setGrossTotalSales(grossTotal);
        response.setTicketsSoldCount(ticketCount);

        return response;
    }
}
