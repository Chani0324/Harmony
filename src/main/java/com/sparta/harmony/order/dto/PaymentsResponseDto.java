package com.sparta.harmony.order.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.harmony.order.entity.Payments;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "결재 응답 Dto")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsResponseDto {

    @Schema(description = "결재 내역 ID", example = "3e995146-5a1d-4fae-b983-7782cdde8660")
    @JsonProperty("payments_id")
    private UUID paymentsId;

    @Schema(description = "가게 ID", example = "fd7e91c0-8a1c-4706-9eb3-0b0ce4d5184b")
    @JsonProperty("store_id")
    private UUID storeId;

    @Schema(description = "가게 이름", example = "교촌치킨")
    @JsonProperty("store_name")
    private String storeName;

    @Schema(description = "주문 ID", example = "fd7e91c0-8a1c-4706-9eb3-0b0ce4d5184b")
    @JsonProperty("order_id")
    private UUID orderId;

    @Schema(description = "결재 총액", example = "124000")
    private int amount;

    public static PaymentsResponseDto fromPayments(Payments payments) {
        return PaymentsResponseDto.builder()
                .paymentsId(payments.getPaymentsId())
                .storeId(payments.getOrder().getStore().getStoreId())
                .storeName(payments.getOrder().getStore().getStoreName())
                .orderId(payments.getOrder().getOrderId())
                .amount(payments.getAmount())
                .build();
    }
}
