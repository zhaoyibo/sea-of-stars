package com.haoyizebo.dingtalk.robot.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yibo
 * @since 2020-08-07
 */
@Data
@Accessors(chain = true)
public class At {

    @Nullable
    private List<String> atMobiles;
    @Nullable
    private boolean isAtAll;

    private boolean isAutoAt = true;

    public At(boolean isAtAll) {
        this.isAtAll = isAtAll;
        this.atMobiles = null;
    }

    public At(List<String> atMobiles) {
        this.atMobiles = atMobiles;
        this.isAtAll = false;
    }

    @JsonProperty("isAtAll")
    public boolean isAtAll() {
        return isAtAll;
    }

    @JsonIgnore
    public boolean isAutoAt() {
        return isAutoAt;
    }

}
