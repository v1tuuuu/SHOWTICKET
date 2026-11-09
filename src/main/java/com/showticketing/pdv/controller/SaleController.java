package com.showticketing.pdv.controller;

import com.showticketing.pdv.dto.SaleRequest;
import com.showticketing.pdv.dto.SaleResponse;
import com.showticketing.pdv.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.processSale(req));
    }
}
