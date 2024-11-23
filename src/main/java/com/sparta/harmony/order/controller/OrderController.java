package com.sparta.harmony.order.controller;

import com.sparta.harmony.common.dto.ApiPageResponseDto;
import com.sparta.harmony.common.dto.ApiResponseDto;
import com.sparta.harmony.common.handler.exception.RestApiException;
import com.sparta.harmony.common.handler.success.SuccessResponseHandler;
import com.sparta.harmony.order.dto.*;
import com.sparta.harmony.order.service.OrderService;
import com.sparta.harmony.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final SuccessResponseHandler successResponseHandler;

    @Operation(summary = "주문 생성", description = "주문을 생성할 때 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주문 요청이 성공적으로 접수된 경우"),
            @ApiResponse(responseCode = "400", description = "주문 요청에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @Parameter(name = "OrderRequestDto", description = "주문 입력 사항", schema = @Schema(implementation = OrderRequestDto.class))
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_OWNER')  or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @PostMapping("/orders")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> createOrder(@RequestBody @Valid OrderRequestDto orderRequestDto,
                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto, userDetails.getUser());

        return successResponseHandler.handleSuccess(HttpStatus.CREATED, "주문이 신청되었습니다. 취소는 5분 이내에만 가능합니다.", orderResponseDto);
    }

    @Operation(summary = "주문 취소(soft delete)", description = "주문을 취소할 때 사용하는 API. USER의 경우 자신의 주문만 취소 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 삭제가 성공적으로 이뤄진경우", content = @Content(schema = @Schema(type = "string", example = "31a87383-f1ca-4c2d-8a9a-b10cb192495b"), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "주문 삭제에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json"))
    })
    @Parameter(name = "orderId", description = "주문 ID", example = "92e09e78-d0fd-4fdd-b335-4971d501bf6c")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_OWNER')  or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @DeleteMapping("/orders/softDelete/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> softDeleteOrder(@PathVariable UUID orderId,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderResponseDto orderResponseDto = orderService.softDeleteOrder(orderId, userDetails.getUser());

        return successResponseHandler.handleSuccess(HttpStatus.OK, "주문이 성공적으로 삭제되었습니다.", orderResponseDto);
    }

    @Operation(summary = "주문 조회", description = "주문을 조회할 때 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 조회가 성공적으로 이뤄진 경우"),
            @ApiResponse(responseCode = "400", description = "주문 조회에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_OWNER')  or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @GetMapping("/orders")
    public ResponseEntity<ApiPageResponseDto<OrderResponseDto>> getOrders(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort_by", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "is_asc", defaultValue = "false") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Page<OrderResponseDto> orderResponseDto = orderService.getOrders(userDetails.getUser(), page - 1, size, sortBy, isAsc);

        return successResponseHandler.handlePageSuccess(HttpStatus.OK, "조회 완료", orderResponseDto);
    }

    @Operation(summary = "주문 상세 조회", description = "주문 상세 조회할 때 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 조회가 성공적으로 이뤄진 경우"),
            @ApiResponse(responseCode = "400", description = "주문 조회에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @Parameter(name = "orderId", description = "주문 ID", example = "92e09e78-d0fd-4fdd-b335-4971d501bf6c")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_OWNER')  or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderDetailResponseDto>> getOrderByOrderId(@PathVariable UUID orderId,
                                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderDetailResponseDto orderDetailResponseDto = orderService.getOrderByOrderId(orderId, userDetails.getUser());

        return successResponseHandler.handleSuccess(HttpStatus.OK, "조회가 완료되었습니다.", orderDetailResponseDto);
    }

    @Operation(summary = "특정 가게 주문 조회", description = "특정 가게의 주문을 조회할 때 사용하는 API. Owner 이상 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 조회가 성공적으로 이뤄진 경우"),
            @ApiResponse(responseCode = "400", description = "주문 조회에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @Parameter(name = "storeId", description = "가게 ID", example = "cab802e2-d172-4ddb-8e25-d2787122a9cc")
    @PreAuthorize("hasAuthority('ROLE_OWNER') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @GetMapping("/orders/store/{storeId}")
    public ResponseEntity<ApiPageResponseDto<OrderResponseDto>> getOrderByStoreId(
            @PathVariable UUID storeId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort_by", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "is_asc", defaultValue = "false") boolean isAsc) {

        Page<OrderResponseDto> orderResponseDto = orderService.getOrdersByStoreId(storeId, page - 1, size, sortBy, isAsc);

        return successResponseHandler.handlePageSuccess(HttpStatus.OK, "조회가 완료되었습니다.", orderResponseDto);
    }

    @Operation(summary = "주문 상태 업데이트", description = "특정 주문의 주문 상태를 수정할 때 사용하는 API. Owner 이상 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정이 성공적으로 이뤄진 경우"),
            @ApiResponse(responseCode = "400", description = "수정에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @Parameter(name = "orderId", description = "주문 ID", example = "92e09e78-d0fd-4fdd-b335-4971d501bf6c")
    @PreAuthorize("hasAuthority('ROLE_OWNER') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderStatusRequestDto orderStatusDto
    ) {
        OrderResponseDto orderResponseDto = orderService.updateOrderStatus(orderId, orderStatusDto);

        return successResponseHandler.handleSuccess(HttpStatus.OK, "수정이 완료되었습니다.", orderResponseDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_OWNER')  or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @DeleteMapping("/orders/cancel/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        OrderResponseDto orderResponseDto = orderService.cancelOrder(orderId, userDetails.getUser());

        return successResponseHandler.handleSuccess(HttpStatus.OK, "주문 취소가 완료되었습니다.", orderResponseDto);
    }
}
