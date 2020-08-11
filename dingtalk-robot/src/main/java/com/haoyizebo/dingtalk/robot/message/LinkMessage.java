package com.haoyizebo.dingtalk.robot.message;

import com.sun.istack.internal.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * link 类型
 *
 * @author yibo
 * @since 2020-08-07
 */
@EqualsAndHashCode(callSuper = true)
public class LinkMessage extends BaseMessage {

    @Getter
    private final Link link;

    public LinkMessage(Link link) {
        super("link");
        this.link = link;
    }


    @Data
    @AllArgsConstructor
    static class Link {
        private String title;
        private String text;
        private String messageUrl;
        @Nullable
        private String picUrl;
    }

}
