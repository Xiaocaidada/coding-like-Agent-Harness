package com.craftmarket.controller;

import com.craftmarket.entity.Vendor;
import com.craftmarket.service.VendorService;
import com.craftmarket.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping
    public Result<?> createVendor(@RequestBody Vendor vendor) {
        return Result.success(vendorService.saveVendor(vendor));
    }

    @GetMapping("/{id}")
    public Result<?> getVendorById(@PathVariable Long id) {
        return Result.success(vendorService.getVendorById(id));
    }

    @PutMapping("/{id}")
    public Result<?> updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return Result.success(vendorService.updateVendor(vendor));
    }

    @GetMapping
    public Result<?> getAllVendors(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String businessType) {
        Page<Vendor> vendors = vendorService.getVendorsPage(page, size, businessType);
        return Result.success(vendors);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteVendor(@PathVariable Long id) {
        return Result.success(vendorService.deleteVendor(id));
    }

    @GetMapping("/user/{userId}")
    public Result<?> getVendorByUserId(@PathVariable Long userId) {
        // 注意：这里需要根据实际业务逻辑实现
        return Result.success(null);
    }
}