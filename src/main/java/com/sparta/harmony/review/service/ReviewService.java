package com.sparta.harmony.review.service;

import com.sparta.harmony.order.repository.OrderRepository;
import com.sparta.harmony.review.dto.ReviewRequestDto;
import com.sparta.harmony.review.dto.ReviewResponseDto;
import com.sparta.harmony.review.entity.Review;
import com.sparta.harmony.review.repository.ReviewRepository;
import com.sparta.harmony.order.entity.Order;
import com.sparta.harmony.store.entity.Store;
import com.sparta.harmony.store.repository.StoreRepository;
import com.sparta.harmony.user.entity.User;
import com.sparta.harmony.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    //리뷰 작성
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Order ID"));
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Store ID"));

        Review review = Review.builder()
                .user(user)
                .store(store)
                .order(order)
                .comment(requestDto.getComment())
                .rating(requestDto.getRating())
                .build();

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // 특정 가게 대한 리뷰 조회
    public List<ReviewResponseDto> getReviewsByStore(UUID storeId) {
        List<Review> reviews = reviewRepository.findByStore_StoreId(storeId);
        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 사용자 대한 리뷰 조회
    public List<ReviewResponseDto> getReviewsByUser(UUID userId) {
        List<Review> reviews = reviewRepository.findByUser_UserId(userId);
        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 주문 대한 리뷰 조회
    public ReviewResponseDto getReviewByOrder(UUID orderId) {
        Review review = reviewRepository.findByOrder_OrderId(orderId);
        return new ReviewResponseDto(review);
    }

    //리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(UUID reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        review.updateReview(requestDto.getComment(), requestDto.getRating());
        return new ReviewResponseDto(review);
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        reviewRepository.delete(review);
    }
}
