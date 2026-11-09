package com.showticketing.pdv.repository;

import com.showticketing.pdv.entity.SaleOrder;
import com.showticketing.pdv.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketId(String ticketId);
    Optional<Ticket> findByTicketCode(String ticketCode);
    List<Ticket> findBySaleOrder(SaleOrder saleOrder);
}
