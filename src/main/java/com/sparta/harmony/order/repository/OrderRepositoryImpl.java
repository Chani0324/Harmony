package com.sparta.harmony.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.harmony.order.entity.Order;
import com.sparta.harmony.order.entity.QOrder;
import com.sparta.harmony.order.entity.QOrderMenu;
import com.sparta.harmony.store.entity.QStore;
import com.sparta.harmony.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findOrderByStoreIdAndDeletedFalse(UUID storeId, Pageable pageable) {
        QOrder order = QOrder.order;
        QStore store = QStore.store;

        var query = queryFactory.selectFrom(order)
                .leftJoin(store).on(order.store.storeId.eq(store.storeId))
                .where(store.storeId.eq(storeId)
                        .and(order.deleted.eq(false)))
                .distinct();

        long total = query.fetch().size();

        var results = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Order> findAllByUserAndDeletedFalseWithFetchJoin(User user, Pageable pageable) {
        QOrder order = QOrder.order;
        QOrderMenu orderMenu = QOrderMenu.orderMenu;

        var query = queryFactory.selectFrom(order)
                .leftJoin(order.orderMenuList, orderMenu)
                .fetchJoin()
                .where(order.user.userId.eq(user.getUserId())
                        .and(order.deleted.eq(false)))
                .distinct();

        long total = query.fetch().size();

        var results = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Order> findAllDeletedFalseWithFetchJoin(Pageable pageable) {
        QOrder order = QOrder.order;
        QOrderMenu orderMenu = QOrderMenu.orderMenu;

        var query = queryFactory.selectFrom(order)
                .leftJoin(order.orderMenuList, orderMenu)
                .where(order.deleted.eq(false))
                .distinct();

        long total = query.fetch().size();

        var results = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }
}

