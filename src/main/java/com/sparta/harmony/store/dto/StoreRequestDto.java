package com.sparta.harmony.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {
    private String storeName;
    private String phoneNumber;
    private String address;
    private String detailAddress;
    private String postcode;
}
