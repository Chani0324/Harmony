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

@Schema(description = "상세 주문 성공 응답 Dto")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetailResponseDto {

    @Schema(description = "주문 ID", example = "fd7e91c0-8a1c-4706-9eb3-0b0ce4d5184b")
    @JsonProperty("order_id")
    private UUID orderId;

    @Schema(description = "가게 이름", example = "교촌치킨")
    @JsonProperty("store_name")
    private String storeName;

    @Schema(description = "총 주문 금액", example = "124000")
    @JsonProperty("total_amount")
    private int totalAmount;

    @Schema(description = "결재 내역 ID", example = "3e995146-5a1d-4fae-b983-7782cdde8660")
    @JsonProperty("payments_id")
    private UUID paymentsId;

    @Schema(description = "주문 메뉴 리스트")
    @JsonProperty("order_menu_list")
    private List<OrderMenuListResponseDto> orderMenuList = new ArrayList<>();

    @Schema(description = "주문 일자", example = "2024-11-17T19:07:04.9538123")
    @JsonProperty("order_date")
    private LocalDateTime createdAt;

    @Schema(description = "주문한 user의 ID", example = "9a5e5b48-b359-48c1-a65f-edac5fd96141")
    @JsonProperty("user_id")
    private UUID userId;

    @Schema(description = "주문 타입", example = "DELIVERY")
    @JsonProperty("order_type")
    private OrderTypeEnum orderType;

    @Schema(description = "주문 상태", example = "PENDING")
    @JsonProperty("order_status")
    private OrderStatusEnum orderStatus;

    @Schema(description = "우편 번호", example = "123123")
    private String postcode;

    @Schema(description = "주소", example = "서울특별시 관악구")
    private String address;

    @Schema(description = "상세 주소", example = "00로 00길 00-0 000호")
    @JsonProperty("detail_address")
    private String detailAddress;

    @Schema(description = "요청 사항", example = "배달 도착 시 집앞에 놔두시고 문만 두드려 주세요")
    @JsonProperty("special_request")
    private String specialRequest;

    public static OrderDetailResponseDto fromOrder(Order order) {
        return OrderDetailResponseDto.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser().getUserId())
                .storeName(order.getStore().getStoreName())
                .totalAmount(order.getTotalAmount())
                .paymentsId(order.getPayments().getPaymentsId())
                .orderMenuList(order.getOrderMenuList().stream()
                        .map(OrderMenuListResponseDto::fromOrderMenu)
                        .toList())
                .createdAt(order.getCreatedAt())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .postcode(order.getAddress().getPostcode())
                .address(order.getAddress().getAddress())
                .detailAddress(order.getAddress().getDetailAddress())
                .specialRequest(order.getSpecialRequest())
                .build();
    }
}
