package com.sparta.harmony.order.entity;

import com.sparta.harmony.store.entity.Store;
import com.sparta.harmony.user.entity.Address;
import com.sparta.harmony.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_order")
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends Timestamped {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderTypeEnum orderType;

    @Column(length = 200, name = "special_request")
    private String specialRequest;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Embedded
    private Address address;

    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderMenu> orderMenuList = new ArrayList<>();

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Payments payments;

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */
    public void addOrderMenu(OrderMenu orderMenu) {
        orderMenuList.add(orderMenu);
        orderMenu.updateOrder(this);
    }

    public void removeOrderMenu(OrderMenu orderMenu) {
        orderMenuList.remove(orderMenu);
        orderMenu.updateOrder(null);
    }

    public void addPayments(Payments payments) {
        this.payments = payments;
        payments.updateOrder(this);
    }

    public void removePayments(Payments payments) {
        this.payments = null;
        payments.updateOrder(null);
    }

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */


    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    public void updateUser(User user) {
        this.user = user;
    }

    public void updateOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }
}
