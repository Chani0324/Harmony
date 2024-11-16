package com.sparta.harmony.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Getter
public class Address {

    private String addrUUID;

    @Column(length = 6)
    private String postcode;

    @Column(length = 200)
    private String address;

    private String detailAddress;

    @Builder
    public Address(String postcode, String address, String detailAddress) {
        this.addrUUID = UUID.randomUUID().toString();
        this.postcode = postcode;
        this.address = address;
        this.detailAddress = detailAddress;
    }
}