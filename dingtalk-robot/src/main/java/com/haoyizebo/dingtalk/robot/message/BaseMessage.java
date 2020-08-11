package com.haoyizebo.dingtalk.robot.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;

/**
 * @author yibo
 * @since 2020-08-07
 */
public abstract class BaseMessage implements IMessage {

    @Getter
    @JsonProperty("msgtype")
    private final String msgType;

    public BaseMessage(String msgType) {
        this.msgType = msgType;
    }

    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();


    @Override
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return toJson();
    }

}
