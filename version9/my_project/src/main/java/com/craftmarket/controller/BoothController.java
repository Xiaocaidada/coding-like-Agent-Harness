package com.craftmarket.controller;

import com.craftmarket.entity.Booth;
import com.craftmarket.service.BoothService;
import com.craftmarket.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booths")
@CrossOrigin(origins = "*")
public class BoothController {

    @Autowired
    private BoothService boothService;

    @PostMapping
    public Result<?> createBooth(@RequestBody Booth booth) {
        return boothService.createBooth(booth);
    }

    @GetMapping("/{id}")
    public Result<?> getBoothById(@PathVariable Long id) {
        return Result.success(boothService.getBoothById(id));
    }

    @PutMapping("/{id}")
    public Result<?> updateBooth(@PathVariable Long id, @RequestBody Booth booth) {
        booth.setId(id);
        return boothService.updateBooth(booth);
    }

    @GetMapping
    public Result<?> getAllBooths(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Page<Booth> booths = boothService.getAllBooths(page, size);
        return Result.success(booths);
    }

    @GetMapping("/market-event/{marketEventId}")
    public Result<?> getBoothsByMarketEvent(@PathVariable Long marketEventId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<Booth> booths = boothService.getBoothsByMarketEvent(marketEventId, page, size);
        return Result.success(booths);
    }

    @GetMapping("/vendor/{vendorId}")
    public Result<?> getBoothsByVendor(@PathVariable Long vendorId,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Page<Booth> booths = boothService.getBoothsByVendor(vendorId, page, size);
        return Result.success(booths);
    }

    @PutMapping("/{id}/allocate")
    public Result<?> allocateBooth(@PathVariable Long id, @RequestParam Long vendorId) {
        return boothService.allocateBooth(id, vendorId);
    }

    @PutMapping("/{id}/release")
    public Result<?> releaseBooth(@PathVariable Long id) {
        return boothService.releaseBooth(id);
    }

    @PutMapping("/{id}/update-status")
    public Result<?> updateBoothStatus(@PathVariable Long id, @RequestParam String status) {
        return boothService.updateBoothStatus(id, status);
    }
}