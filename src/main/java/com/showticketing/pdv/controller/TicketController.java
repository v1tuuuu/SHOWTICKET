package com.showticketing.pdv.controller;

import com.showticketing.pdv.dto.CheckInRequest;
import com.showticketing.pdv.dto.CheckInResponse;
import com.showticketing.pdv.dto.TicketResponse;
import com.showticketing.pdv.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponse> checkIn(@Valid @RequestBody CheckInRequest req) {
        return ResponseEntity.ok(ticketService.validateCheckIn(req));
    }

    @GetMapping("/{ticketCode}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable String ticketCode) {
        return ResponseEntity.ok(ticketService.getTicketDetails(ticketCode));
    }
}
