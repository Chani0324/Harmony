package com.sparta.harmony.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.harmony.order.entity.Payments;
import com.sparta.harmony.order.entity.QOrder;
import com.sparta.harmony.order.entity.QPayments;
import com.sparta.harmony.store.entity.QStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RequiredArgsConstructor
public class PaymentsRepositoryImpl implements PaymentsRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Payments> findPaymentsByStoreIdAndDeletedFalse(UUID storeId, Pageable pageable) {
        QPayments payments = QPayments.payments;
        QOrder order = QOrder.order;
        QStore store = QStore.store;

        var query = queryFactory.selectFrom(payments)
                .join(order).on(payments.order.orderId.eq(order.orderId))
                .join(store).on(order.store.storeId.eq(store.storeId))
                .where(store.storeId.eq(storeId)
                        .and(payments.deleted.eq(false)));

        long total = query.fetch().size();

        var results = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }
}
