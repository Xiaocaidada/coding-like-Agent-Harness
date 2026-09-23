package com.craftmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class VendorDTO {
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "摊位名称不能为空")
    @Size(max = 100, message = "摊位名称长度不能超过100个字符")
    private String boothName;

    @NotBlank(message = "摊位描述不能为空")
    @Size(max = 500, message = "摊位描述长度不能超过500个字符")
    private String description;

    @Size(max = 200, message = "产品类型长度不能超过200个字符")
    private String productType;

    @Size(max = 200, message = "特色产品长度不能超过200个字符")
    private String specialtyProducts;

    @Size(max = 200, message = "联系方式长度不能超过200个字符")
    private String contact;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VendorDTO() {}

    public VendorDTO(Long userId, String boothName, String description, String productType, 
                     String specialtyProducts, String contact) {
        this.userId = userId;
        this.boothName = boothName;
        this.description = description;
        this.productType = productType;
        this.specialtyProducts = specialtyProducts;
        this.contact = contact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSpecialtyProducts() {
        return specialtyProducts;
    }

    public void setSpecialtyProducts(String specialtyProducts) {
        this.specialtyProducts = specialtyProducts;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}