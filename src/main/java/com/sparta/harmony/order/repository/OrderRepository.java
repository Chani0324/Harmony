package com.sparta.harmony.order.repository;

import com.sparta.harmony.order.entity.Order;
import com.sparta.harmony.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {

    Page<Order> findAllByDeletedFalse(Pageable pageable);

    Optional<Order> findByOrderIdAndUserAndDeletedFalse(UUID orderId, User user);

    Optional<Order> findByOrderIdAndDeletedFalse(UUID orderId);

    Page<Order> findAllByUserAndDeletedFalse(User user, Pageable pageable);
}
