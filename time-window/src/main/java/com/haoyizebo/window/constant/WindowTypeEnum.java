package com.haoyizebo.window.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yibo
 * @since 2020-11-02
 */
@Getter
@AllArgsConstructor
public enum WindowTypeEnum {
    /**
     *
     */
    SLIDING("ws"),
    FIXED("wf"),
    ;

    private final String prefix;

}
