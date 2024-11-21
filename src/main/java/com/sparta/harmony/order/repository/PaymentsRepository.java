package com.sparta.harmony.order.repository;

import com.sparta.harmony.order.entity.Payments;
import com.sparta.harmony.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, UUID>, PaymentsRepositoryCustom {

    Page<Payments> findAllByDeletedFalse(Pageable pageable);

    Optional<Payments> findByPaymentsIdAndUserAndDeletedFalse(UUID paymentsId, User user);

    Optional<Payments> findByPaymentsIdAndDeletedFalse(UUID paymentsId);

    Page<Payments> findAllByUserAndDeletedFalse(User user, Pageable pageable);
}
