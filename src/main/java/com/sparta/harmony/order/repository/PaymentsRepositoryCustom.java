package com.sparta.harmony.order.repository;

import com.sparta.harmony.order.entity.Payments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentsRepositoryCustom {
    Page<Payments> findPaymentsByStoreIdAndDeletedFalse(UUID storeId, Pageable pageable);
}
