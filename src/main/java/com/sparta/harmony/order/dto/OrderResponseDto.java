package com.sparta.harmony.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.harmony.order.entity.Order;
import com.sparta.harmony.order.entity.OrderStatusEnum;
import com.sparta.harmony.order.entity.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "주문 성공 응답 Dto")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponseDto {

    @Schema(description = "주문 ID", example = "fd7e91c0-8a1c-4706-9eb3-0b0ce4d5184b")
    @JsonProperty("order_id")
    private UUID orderId;

    @Schema(description = "가게 이름", example = "교촌치킨")
    @JsonProperty("store_name")
    private String storeName;

    @Schema(description = "총 주문 금액", example = "124000")
    @JsonProperty("total_amount")
    private int totalAmount;

    @Schema(description = "주문 메뉴 리스트")
    @JsonProperty("order_menu_list")
    private List<OrderMenuListResponseDto> orderMenuList = new ArrayList<>();

    @Schema(description = "주문 일자", example = "2024-11-17T19:07:04.9538123")
    @JsonProperty("order_date")
    private LocalDateTime createdAt;

    @Schema(description = "주문 타입", example = "DELIVERY")
    @JsonProperty("order_type")
    private OrderTypeEnum orderType;

    @Schema(description = "주문 상태", example = "PENDING")
    @JsonProperty("order_status")
    private OrderStatusEnum orderStatus;

    public static OrderResponseDto fromOrder(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .storeName(order.getStore().getStoreName())
                .totalAmount(order.getTotalAmount())
                .orderMenuList(order.getOrderMenuList().stream()
                        .map(OrderMenuListResponseDto::fromOrderMenu)
                        .toList())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
