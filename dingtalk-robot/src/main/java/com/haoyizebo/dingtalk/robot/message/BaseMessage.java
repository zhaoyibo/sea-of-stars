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

    protected static final String LINE_SEPARATOR = "\n\n";

    @Getter
    @JsonProperty("msgtype")
    private final String msgType;

    public BaseMessage(String msgType) {
        this.msgType = msgType;
    }

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();


    @Override
    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return toJson();
    }

}
