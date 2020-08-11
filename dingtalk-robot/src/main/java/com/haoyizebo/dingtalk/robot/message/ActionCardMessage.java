package com.haoyizebo.dingtalk.robot.message;

import com.sun.istack.internal.Nullable;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * ActionCard 类型，包括 整体跳转 和 独立跳转
 * <p>
 * 整体跳转
 * <p>
 * <img src="https://img.alicdn.com/tfs/TB1ehWCiBfH8KJjy1XbXXbLdXXa-334-218.png"></img>
 * <p>
 * 独立跳转
 * <p>
 * <img src="https://ding-doc.oss-cn-beijing.aliyuncs.com/images/0.0.361/1570679939723-c1fb7861-5bcb-4c30-9e1b-033932f6b72f.png"></img>
 *
 * @author yibo
 * @since 2020-08-07
 */
@EqualsAndHashCode(callSuper = true)
public class ActionCardMessage extends BaseMessage {

    @Getter
    private final ActionCard actionCard;

    public ActionCardMessage(ActionCardMessage.ActionCard actionCard) {
        super("actionCard");
        this.actionCard = actionCard;
    }

    @RequiredArgsConstructor
    @Data
    @Accessors(chain = true)
    public static class ActionCard {
        @NonNull
        private String title;
        @NonNull
        private String text;
        /**
         * 与 btns 二选一
         */
        private String singleTitle;
        /**
         * 与 btns 二选一
         */
        private String singleURL;
        /**
         * 与 singleTitle&singleURL 二选一
         */
        private List<Btn> btns;
        /**
         * 0 - 按钮竖直排列，1 - 按钮横向排列
         */
        @Nullable
        private String btnOrientation;
    }

    @AllArgsConstructor
    @Data
    static class Btn {
        private String title;
        private String actionURL;
    }

}
