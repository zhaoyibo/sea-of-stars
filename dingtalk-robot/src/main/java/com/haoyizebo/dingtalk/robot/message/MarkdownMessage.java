package com.haoyizebo.dingtalk.robot.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * markdown 类型
 * <p>
 * <img src="https://img.alicdn.com/tfs/TB1yL3taUgQMeJjy0FeXXXOEVXa-492-380.png"></img>
 *
 * @author yibo
 * @since 2020-08-07
 */
@EqualsAndHashCode(callSuper = true)
public class MarkdownMessage extends BaseMessage implements HasAt {

    @Getter
    private final Markdown markdown;
    @Getter
    private final At at;

    public MarkdownMessage(Markdown markdown, At at) {
        super("markdown");
        this.markdown = markdown;
        this.at = at;
        autoAt();
    }

    @Override
    public String getBodyText() {
        return markdown.getText();
    }

    @Override
    public void setBodyText(String text) {
        this.markdown.setText(text);
    }

    @AllArgsConstructor
    @Data
    public static class Markdown {
        private String title;
        private String text;
    }


}
