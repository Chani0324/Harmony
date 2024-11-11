package com.sparta.harmony.store.controller;

import com.sparta.harmony.store.dto.StoreRequestDto;
import com.sparta.harmony.store.entity.Store;
import com.sparta.harmony.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping
    public Store createStore(@RequestBody StoreRequestDto storeRequestDto) {
        return storeService.createStore(storeRequestDto);
    }
}
