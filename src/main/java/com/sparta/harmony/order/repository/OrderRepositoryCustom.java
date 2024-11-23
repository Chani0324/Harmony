package com.sparta.harmony.order.repository;

import com.sparta.harmony.order.entity.Order;
import com.sparta.harmony.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepositoryCustom {
    Page<Order> findOrderByStoreIdAndDeletedFalse(UUID storeId, Pageable pageable);

    Page<Order> findAllByUserAndDeletedFalseWithFetchJoin(User user, Pageable pageable);

    Page<Order> findAllDeletedFalseWithFetchJoin(Pageable pageable);

}
