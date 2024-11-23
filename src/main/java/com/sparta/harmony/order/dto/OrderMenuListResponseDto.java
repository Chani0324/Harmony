package com.sparta.harmony.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.harmony.order.entity.OrderMenu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Schema(description = "주문 성공 메뉴 리스트")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderMenuListResponseDto {

    @Schema(description = "주문 메뉴의 ID", example = "cf5ab475-6109-4416-b51f-aa85f8c35329")
    @JsonProperty("menu_id")
    private UUID menuId;

    @Schema(description = "주문한 메뉴의 이름", example = "양념 치킨")
    @JsonProperty("menu_name")
    private String menuName;

    @Schema(description = "주문 메뉴의 양", example = "4")
    @JsonProperty("quantity")
    private int quantity;

    public static OrderMenuListResponseDto fromOrderMenu(OrderMenu orderMenu) {
        return OrderMenuListResponseDto.builder()
                .menuId(orderMenu.getMenu().getMenuId())
                .menuName(orderMenu.getMenu().getName())
                .quantity(orderMenu.getQuantity())
                .build();
    }
}
