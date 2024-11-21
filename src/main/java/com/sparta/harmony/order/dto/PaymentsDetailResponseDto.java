package com.sparta.harmony.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.harmony.order.entity.Payments;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "상세 결재 응답 Dto")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsDetailResponseDto {

    @Schema(description = "결재 내역 ID", example = "3e995146-5a1d-4fae-b983-7782cdde8660")
    @JsonProperty("payments_id")
    private UUID paymentsId;

    @Schema(description = "가게 ID", example = "fd7e91c0-8a1c-4706-9eb3-0b0ce4d5184b")
    @JsonProperty("store_id")
    private UUID storeId;

    @Schema(description = "주문 ID", example = "fd7e91c0-8a1c-4706-9eb3-0b0ce4d5184b")
    @JsonProperty("order_id")
    private UUID orderId;

    @Schema(description = "주문한 유저의 email", example = "user@user.com")
    private String email;

    @Schema(description = "결재 총액", example = "124000")
    private int amount;

    @Schema(description = "결재 일자", example = "2024-11-17T19:07:04.9538123")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "가게 이름", example = "교촌치킨")
    @JsonProperty("store_name")
    private String storeName;

    @Schema(description = "주문 메뉴 리스트")
    private List<OrderMenuListResponseDto> orderMenuList = new ArrayList<>();

    public static PaymentsDetailResponseDto fromPayments(Payments payments) {
        return PaymentsDetailResponseDto.builder()
                .paymentsId(payments.getPaymentsId())
                .storeId(payments.getOrder().getStore().getStoreId())
                .orderId(payments.getOrder().getOrderId())
                .email(payments.getUser().getEmail())
                .storeName(payments.getOrder().getStore().getStoreName())
                .orderMenuList(payments.getOrder().getOrderMenuList().stream()
                        .map(OrderMenuListResponseDto::fromOrderMenu)
                        .toList())
                .amount(payments.getAmount())
                .createdAt(payments.getCreatedAt())
                .build();
    }
}
