package com.haoyizebo.dingtalk.robot.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @author yibo
 * @since 2020-08-07
 */
public interface HasAt {

    At getAt();

    @JsonIgnore
    String getBodyText();

    void setBodyText(String text);

    default void autoAt() {
        if (!getAt().isAutoAt()) {
            return;
        }
        StringBuilder builder = new StringBuilder(getBodyText());
        if (getAt().isAtAll()) {
            builder.append("  \n  @所有人");
        } else {
            List<String> atMobiles = getAt().getAtMobiles();
            if (atMobiles != null && !atMobiles.isEmpty()) {
                builder.append("  \n  ");
                for (String atMobile : atMobiles) {
                    builder.append("@").append(atMobile).append(" ");
                }
            }
        }
        setBodyText(builder.toString());
    }

}
