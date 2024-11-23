package com.sparta.harmony.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.harmony.order.entity.Order;
import com.sparta.harmony.order.entity.QOrder;
import com.sparta.harmony.order.entity.QOrderMenu;
import com.sparta.harmony.order.entity.QPayments;
import com.sparta.harmony.store.entity.QStore;
import com.sparta.harmony.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findOrderByStoreIdAndDeletedFalse(UUID storeId, Pageable pageable) {
        QOrder order = QOrder.order;
        QStore store = QStore.store;

        var query = queryFactory.selectFrom(order)
                .join(store).on(order.store.storeId.eq(store.storeId))
                .where(store.storeId.eq(storeId)
                        .and(order.deleted.eq(false)));

        long total = query.fetch().size();

        var results = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<Order> findAllWithFetchJoin() {
        QOrder order = QOrder.order;
        QOrderMenu orderMenu = QOrderMenu.orderMenu;
        QStore store = QStore.store;
        QUser user = QUser.user;
        QPayments payments = QPayments.payments;

        return queryFactory.selectFrom(order)
                .leftJoin(order.orderMenuList, orderMenu)
                .fetchJoin()
                .leftJoin(order.store, store)
                .fetchJoin()
                .leftJoin(order.user, user)
                .fetchJoin()
                .leftJoin(order.payments, payments)
                .fetchJoin()
                .distinct()
                .fetch();
    }
}

