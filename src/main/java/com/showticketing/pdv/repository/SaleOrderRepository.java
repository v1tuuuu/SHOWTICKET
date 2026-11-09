package com.showticketing.pdv.repository;

import com.showticketing.pdv.entity.CashSession;
import com.showticketing.pdv.entity.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {
    Optional<SaleOrder> findByOrderCode(String orderCode);
    List<SaleOrder> findByCashSessionOrderByCreatedAtDesc(CashSession session);
}
