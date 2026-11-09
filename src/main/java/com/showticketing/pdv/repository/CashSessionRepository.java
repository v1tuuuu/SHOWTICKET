package com.showticketing.pdv.repository;

import com.showticketing.pdv.entity.CashSession;
import com.showticketing.pdv.enums.CashSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashSessionRepository extends JpaRepository<CashSession, Long> {
    Optional<CashSession> findBySessionId(String sessionId);
    Optional<CashSession> findFirstByStatusOrderByOpenedAtDesc(CashSessionStatus status);
}
