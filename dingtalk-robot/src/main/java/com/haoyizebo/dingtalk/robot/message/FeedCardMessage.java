package com.haoyizebo.dingtalk.robot.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * FeedCard 类型
 * <p>
 * <img src="http://img01.taobaocdn.com/top/i1/LB1R2evQVXXXXXDapXXXXXXXXXX"></img>
 *
 * @author yibo
 * @since 2020-08-07
 */
@EqualsAndHashCode(callSuper = true)
public class FeedCardMessage extends BaseMessage {

    @Getter
    private final FeedCard feedCard;

    public FeedCardMessage(List<FeedCardMessage.Link> links) {
        super("feedCard");
        this.feedCard = new FeedCard(links);
    }

    @AllArgsConstructor
    @Data
    public static class FeedCard {
        private List<Link> links;
    }

    @AllArgsConstructor
    @Data
    public static class Link {
        private String title;
        private String messageURL;
        private String picURL;
    }

}
