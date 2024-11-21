package com.sparta.harmony.order.service;

import com.sparta.harmony.order.dto.PaymentsDetailResponseDto;
import com.sparta.harmony.order.dto.PaymentsResponseDto;
import com.sparta.harmony.order.entity.Payments;
import com.sparta.harmony.order.repository.PaymentsRepository;
import com.sparta.harmony.user.entity.Role;
import com.sparta.harmony.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;

    @Transactional(readOnly = true)
    public Page<PaymentsResponseDto> getPayments(User user, int page, int size
            , String sortBy, boolean isAsc) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);
        Page<Payments> paymentsList;

        Role userRoleEnum = user.getRole();

        if (userRoleEnum == Role.USER || userRoleEnum == Role.OWNER) {
            paymentsList = paymentsRepository.findAllByUserAndDeletedFalse(user, pageable);
        } else {
            paymentsList = paymentsRepository.findAllByDeletedFalse(pageable);
        }

        return paymentsList.map(PaymentsResponseDto::fromPayments);
    }

    @Transactional(readOnly = true)
    public Page<PaymentsResponseDto> getPaymentsByStoreId(UUID storeId, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);
        Page<Payments> paymentsList;
        paymentsList = paymentsRepository.findPaymentsByStoreIdAndDeletedFalse(storeId, pageable);

        return paymentsList.map(PaymentsResponseDto::fromPayments);
    }

    public PaymentsDetailResponseDto getPaymentsByPaymentsId(UUID paymentsId, User user) {
        Role userRoleEnum = user.getRole();
        Payments payments;

        if (userRoleEnum == Role.USER || userRoleEnum == Role.OWNER) {
            payments = paymentsRepository.findByPaymentsIdAndUserAndDeletedFalse(paymentsId, user).orElseThrow(
                    () -> new IllegalArgumentException("고객님의 결재 내역이 없습니다."));
        } else {
            payments = paymentsRepository.findByPaymentsIdAndDeletedFalse(paymentsId).orElseThrow(
                    () -> new IllegalArgumentException("해당 결재 내역이 없습니다."));
        }

        return PaymentsDetailResponseDto.fromPayments(payments);
    }

    private Pageable getPageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
}
