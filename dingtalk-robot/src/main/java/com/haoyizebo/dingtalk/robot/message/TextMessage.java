package com.haoyizebo.dingtalk.robot.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * text 类型
 * <p>
 * <img src="https://ding-doc.oss-cn-beijing.aliyuncs.com/images/0.0.361/1570679827267-6243216b-d1c3-48b7-9b1e-0f0b4211b50b.png"></img>
 *
 * @author yibo
 * @since 2020-08-07
 */
@EqualsAndHashCode(callSuper = true)
public class TextMessage extends BaseMessage implements HasAt {

    @Getter
    private final Text text;
    @Getter
    private final At at;

    public TextMessage(String content, At at) {
        super("text");
        this.text = new Text(content);
        this.at = at;
        autoAt();
    }

    @Override
    public String getBodyText() {
        return text.getContent();
    }

    @Override
    public void setBodyText(String text) {
        this.text.setContent(text);
    }

    @Data
    @AllArgsConstructor
    static class Text {
        private String content;
    }

}
