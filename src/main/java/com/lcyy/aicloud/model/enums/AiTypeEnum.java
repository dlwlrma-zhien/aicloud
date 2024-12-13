package com.lcyy.aicloud.model.enums;

public enum AiTypeEnum {
    CHAT(1),
    DRAW(2);

    private final Integer value;

    AiTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
