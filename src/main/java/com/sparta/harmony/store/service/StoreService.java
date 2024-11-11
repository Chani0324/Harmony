package com.sparta.harmony.store.service;

import com.sparta.harmony.store.dto.StoreRequestDto;
import com.sparta.harmony.store.entity.Store;
import com.sparta.harmony.store.repository.StoreRepository;
import com.sparta.harmony.user.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Store createStore(StoreRequestDto storeRequestDto) {
        Address address = new Address(
                storeRequestDto.getAddress(),
                storeRequestDto.getDetailAddress(),
                storeRequestDto.getPostcode()
        );

        Store store = Store.builder()
                .storeName(storeRequestDto.getStoreName())
                .phoneNumber(storeRequestDto.getPhoneNumber())
                .address(address)
                .storeId(UUID.randomUUID())
                .build();

        return storeRepository.save(store);
    }

    
}
