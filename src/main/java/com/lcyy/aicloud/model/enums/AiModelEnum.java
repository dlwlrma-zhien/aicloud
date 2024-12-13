package com.lcyy.aicloud.model.enums;

/**
 * @author: dlwlrma
 * @data 2024年11月05日 22:39
 * @Description: TODO:
 */
public enum AiModelEnum {
    OPENAI(1),
    TONGYIQIANWEN(2),
    XUNFEIXINGHUO(3),
    WENXINYIYAN(4),
    DOUBAO(5),
    LOCAL_MODEL_AI(6);

    private final Integer value;

    AiModelEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
