package com.craftmarket.enums;

public enum MarketEventStatus {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    ENDED("已结束");

    private final String description;

    MarketEventStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}