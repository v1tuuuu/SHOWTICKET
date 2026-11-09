package com.showticketing.pdv.controller;

import com.showticketing.pdv.dto.*;
import com.showticketing.pdv.entity.CashMovement;
import com.showticketing.pdv.entity.CashSession;
import com.showticketing.pdv.service.CashSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash-session")
@CrossOrigin(origins = "*")
public class CashSessionController {

    private final CashSessionService cashSessionService;

    public CashSessionController(CashSessionService cashSessionService) {
        this.cashSessionService = cashSessionService;
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSession() {
        return cashSessionService.findCurrentActiveSessionOptional()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping("/open")
    public ResponseEntity<CashSession> openSession(@Valid @RequestBody CashSessionOpenRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cashSessionService.openSession(req));
    }

    @PostMapping("/{sessionId}/movement")
    public ResponseEntity<CashMovement> addMovement(@PathVariable String sessionId,
                                                    @Valid @RequestBody CashMovementRequest req) {
        return ResponseEntity.ok(cashSessionService.addMovement(sessionId, req));
    }

    @PostMapping("/{sessionId}/close")
    public ResponseEntity<CashSessionSummaryResponse> closeSession(@PathVariable String sessionId,
                                                                  @Valid @RequestBody CashSessionCloseRequest req) {
        return ResponseEntity.ok(cashSessionService.closeSession(sessionId, req));
    }

    @GetMapping("/{sessionId}/summary")
    public ResponseEntity<CashSessionSummaryResponse> getSummary(@PathVariable String sessionId) {
        return ResponseEntity.ok(cashSessionService.getSessionSummary(sessionId));
    }
}
