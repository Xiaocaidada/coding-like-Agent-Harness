package com.craftmarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ApplicationDTO {
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "市场活动ID不能为空")
    private Long marketEventId;

    @Size(max = 500, message = "申请描述长度不能超过500个字符")
    private String description;

    @Size(max = 200, message = "期望位置长度不能超过200个字符")
    private String preferredLocation;

    @Size(max = 200, message = "产品类型长度不能超过200个字符")
    private String productType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ApplicationDTO() {}

    public ApplicationDTO(Long userId, Long marketEventId, String description, 
                         String preferredLocation, String productType) {
        this.userId = userId;
        this.marketEventId = marketEventId;
        this.description = description;
        this.preferredLocation = preferredLocation;
        this.productType = productType;
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

    public Long getMarketEventId() {
        return marketEventId;
    }

    public void setMarketEventId(Long marketEventId) {
        this.marketEventId = marketEventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
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