package com.showticketing.pdv.repository;

import com.showticketing.pdv.entity.CashMovement;
import com.showticketing.pdv.entity.CashSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashMovementRepository extends JpaRepository<CashMovement, Long> {
    List<CashMovement> findByCashSessionOrderByTimestampDesc(CashSession session);
}
