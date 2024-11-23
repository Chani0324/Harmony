package com.sparta.harmony.order.repository;

import com.sparta.harmony.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepositoryCustom {
    Page<Order> findOrderByStoreIdAndDeletedFalse(UUID storeId, Pageable pageable);

    List<Order> findAllWithFetchJoin();

}
