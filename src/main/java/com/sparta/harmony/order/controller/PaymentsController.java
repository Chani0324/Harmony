package com.sparta.harmony.order.controller;

import com.sparta.harmony.common.dto.ApiPageResponseDto;
import com.sparta.harmony.common.dto.ApiResponseDto;
import com.sparta.harmony.common.handler.exception.RestApiException;
import com.sparta.harmony.common.handler.success.SuccessResponseHandler;
import com.sparta.harmony.order.dto.PaymentsDetailResponseDto;
import com.sparta.harmony.order.dto.PaymentsResponseDto;
import com.sparta.harmony.order.service.PaymentsService;
import com.sparta.harmony.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class PaymentsController {

    private final PaymentsService paymentsService;
    private final SuccessResponseHandler successResponseHandler;

    @Operation(summary = "결제 내역 조회", description = "결제 내역을 조회할 때 사용하는 API. User/Owner의 경우 자기 자신 결재 내역. manager/master의 경우 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 내역 조회에 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "결제 내역 조회에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_OWNER')  or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @GetMapping("/payments")
    public ResponseEntity<ApiPageResponseDto<PaymentsResponseDto>> getPayments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort_by", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "is_asc", defaultValue = "false") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Page<PaymentsResponseDto> paymentsResponseDto = paymentsService.getPayments(userDetails.getUser(), page - 1, size, sortBy, isAsc);

        return successResponseHandler.handlePageSuccess(HttpStatus.OK, "조회에 성공하였습니다.", paymentsResponseDto);
    }


    @Operation(summary = "특정 가게 결제 내역 조회", description = "특정 가게 결제 내역을 조회할 때 사용하는 API. owner 이상만 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 내역 조회에 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "결제 내역 조회에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @Parameter(name = "storeId", description = "가게 ID", example = "cab802e2-d172-4ddb-8e25-d2787122a9cc")
    @PreAuthorize("hasAuthority('ROLE_OWNER') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @GetMapping("/payments/store/{storeId}")
    public ResponseEntity<ApiPageResponseDto<PaymentsResponseDto>> getPaymentsByStoreId(
            @PathVariable UUID storeId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort_by", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "is_asc", defaultValue = "false") boolean isAsc
    ) {
        Page<PaymentsResponseDto> paymentsResponseDto = paymentsService.getPaymentsByStoreId(storeId, page - 1, size, sortBy, isAsc);

        return successResponseHandler.handlePageSuccess(HttpStatus.OK, "조회에 성공하였습니다.", paymentsResponseDto);
    }

    @Operation(summary = "결제 내역 상세 조회", description = "결제 내역을 상세 조회할 때 사용하는 API. user/owner의 경우 자신의 내역만, manager/master의 경우 모든 내역에 대해 조회 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 내역 조회에 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "결제 내역 조회에 실패했을 경우", content = @Content(schema = @Schema(implementation = RestApiException.class), mediaType = "application/json")),
    })
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_OWNER')  or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_MASTER')")
    @GetMapping("/payments/{paymentsId}")
    public ResponseEntity<ApiResponseDto<PaymentsDetailResponseDto>> getPaymentsByPaymentsId(@PathVariable UUID paymentsId,
                                                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PaymentsDetailResponseDto paymentsDetailResponseDto = paymentsService.getPaymentsByPaymentsId(paymentsId, userDetails.getUser());

        return successResponseHandler.handleSuccess(HttpStatus.OK, "조회에 성공하였습니다.", paymentsDetailResponseDto);
    }
}
