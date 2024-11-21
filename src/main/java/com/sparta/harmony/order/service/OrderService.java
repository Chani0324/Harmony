package com.sparta.harmony.order.service;

import com.sparta.harmony.menu.repository.MenuRepository;
import com.sparta.harmony.order.dto.*;
import com.sparta.harmony.order.entity.*;
import com.sparta.harmony.order.repository.OrderMenuRepository;
import com.sparta.harmony.order.repository.OrderRepository;
import com.sparta.harmony.store.repository.StoreRepository;
import com.sparta.harmony.user.entity.Address;
import com.sparta.harmony.user.entity.Role;
import com.sparta.harmony.user.entity.User;
import com.sparta.harmony.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final OrderMenuRepository orderMenuRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, User user) {

        User userInfo = userRepository.findByEmail(user.getEmail()).orElseThrow(()
                -> new IllegalArgumentException("유저 정보를 확인해 주세요"));

        Address address;

        if ((orderRequestDto.getAddress().isEmpty())
                && (orderRequestDto.getDetailAddress().isEmpty())) {
            if (orderRequestDto.getOrderType().equals(OrderTypeEnum.TAKEOUT)) {
                UUID storeId = orderRequestDto.getStoreId();
                Address storeAddress = storeRepository.findById(storeId).orElseThrow(
                        () -> new IllegalArgumentException("가게 ID를 확인해주세요")).getAddress();

                address = buildAddressUseAddress(storeAddress);
            } else {
                Address basicUserAddress = userInfo.getAddress();

                address = buildAddressUseAddress(basicUserAddress);
            }

        } else {
            address = buildAddressUseDto(orderRequestDto);
        }

        int total_price = getTotalPrice(orderRequestDto);
        Order order = buildOrder(orderRequestDto, address, userInfo, total_price);
        Payments payments = buildPayments(userInfo, total_price, order);
        buildMenuList(orderRequestDto, order);

        order.addPayments(payments);
        userInfo.addOrder(order);
        userInfo.addPayments(payments);

        orderRepository.save(order);

        return OrderResponseDto.fromOrder(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(User user, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);

        Role userRoleEnum = user.getRole();

        Page<Order> orderList = isUserOrOwner(userRoleEnum)
                ? orderRepository.findAllByUserAndDeletedFalse(user, pageable)
                : orderRepository.findAllByDeletedFalse(pageable);

        return orderList.map(OrderResponseDto::fromOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByStoreId(UUID storeId, int page, int size,
                                                     String sortBy, boolean isAsc) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);
        Page<Order> orderList = orderRepository.findOrderByStoreIdAndDeletedFalse(storeId, pageable);

        return orderList.map(OrderResponseDto::fromOrder);
    }

    public OrderDetailResponseDto getOrderByOrderId(UUID orderId, User user) {
        Role userRoleEnum = user.getRole();
        Order order;

        if (isUserOrOwner(userRoleEnum)) {
            order = orderRepository.findByOrderIdAndUserAndDeletedFalse(orderId, user).orElseThrow(()
                    -> new IllegalArgumentException("고객님의 주문 내용이 있는지 확인해주세요."));
        } else {
            order = orderRepository.findByOrderIdAndDeletedFalse(orderId).orElseThrow(()
                    -> new IllegalArgumentException("없는 주문 번호 입니다."));
        }

        return OrderDetailResponseDto.fromOrder(order);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatusRequestDto orderStatusDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("없는 주문 번호입니다."));

        order.updateOrderStatus(orderStatusDto.getOrderStatus());
        return OrderResponseDto.fromOrder(order);
    }

    @Transactional
    public OrderResponseDto softDeleteOrder(UUID orderId, User user) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        LocalDateTime orderTime = order.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        long secondDiff = Duration.between(orderTime, now).getSeconds();

        if (secondDiff >= 300) {
            throw new IllegalArgumentException("주문 시간이 5분이 넘어 취소가 불가능합니다.");
        }

        Role userRoleEnum = user.getRole();
        String email = user.getEmail();

        if (isUser(userRoleEnum)) {
            UUID userId = user.getUserId();
            UUID orderUserId = order.getUser().getUserId();

            if (!userId.equals(orderUserId)) {
                throw new IllegalArgumentException("다른 유저의 주문은 취소할 수 없습니다.");
            }
        }
        List<OrderMenu> orderMenuList = orderMenuRepository.findAllByOrder(order);

        for (OrderMenu orderMenu : orderMenuList) {
            orderMenu.softDelete(email);
        }

        order.softDelete(email);
        order.updateOrderStatus(OrderStatusEnum.CANCELED);
        orderRepository.save(order);
        orderMenuRepository.saveAll(orderMenuList);

        return OrderResponseDto.fromOrder(order);
    }

    private boolean isUser(Role userRoleEnum) {
        return userRoleEnum.equals(Role.USER);
    }

    private boolean isUserOrOwner(Role userRoleEnum) {
        return isUser(userRoleEnum) || userRoleEnum.equals(Role.OWNER);
    }

    private Pageable getPageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }

    private void buildMenuList(OrderRequestDto orderRequestDto, Order order) {
        for (OrderMenuListRequestDto menuItem : orderRequestDto.getOrderMenuList()) {
            OrderMenu orderMenu = OrderMenu.builder()
                    .quantity(menuItem.getQuantity())
                    .order(order)
                    .menu(menuRepository.findById(menuItem.getMenuId()).orElseThrow(() -> new IllegalArgumentException("해당 주문 메뉴 ID는 없습니다.")))
                    .build();
            order.addOrderMenu(orderMenu);
        }
    }

    private Address buildAddressUseAddress(Address basicUserAddress) {
        return Address.builder()
                .postcode(basicUserAddress.getPostcode())
                .address(basicUserAddress.getAddress())
                .detailAddress(basicUserAddress.getDetailAddress())
                .build();
    }

    private Address buildAddressUseDto(OrderRequestDto orderRequestDto) {
        return Address.builder()
                .postcode(orderRequestDto.getPostcode())
                .address(orderRequestDto.getAddress())
                .detailAddress(orderRequestDto.getDetailAddress())
                .build();

    }

    private Payments buildPayments(User userInfo, int total_price, Order order) {
        return Payments.builder()
                .user(userInfo)
                .order(order)
                .amount(total_price).build();
    }

    private Order buildOrder(OrderRequestDto orderRequestDto, Address address, User userInfo, int total_price) {
        return Order.builder()
                .orderStatus(OrderStatusEnum.PENDING)
                .orderType(orderRequestDto.getOrderType())
                .specialRequest(orderRequestDto.getSpecialRequest())
                .totalAmount(total_price).address(address)
                .user(userInfo)
                // 초기화 안해주면 null값 들어가서 값을 못가져옴. 반드시 초기화 필요
                .orderMenuList(new ArrayList<>())
                .store(storeRepository.findById(orderRequestDto.getStoreId()).orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다.")))
                .build();
    }


    private int getTotalPrice(OrderRequestDto orderRequestDto) {
        int total_price = 0;

        for (OrderMenuListRequestDto menuItem : orderRequestDto.getOrderMenuList()) {
            int price = menuRepository.findById(menuItem.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 없습니다.")).getPrice();
            int quantity = menuItem.getQuantity();
            total_price += price * quantity;
        }
        return total_price;
    }
}
