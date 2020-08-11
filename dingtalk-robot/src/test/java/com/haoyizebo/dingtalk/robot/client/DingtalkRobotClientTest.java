package com.haoyizebo.dingtalk.robot.client;

import com.haoyizebo.dingtalk.robot.exception.InvalidKeyException;
import com.haoyizebo.dingtalk.robot.message.At;
import com.haoyizebo.dingtalk.robot.message.IMessage;
import com.haoyizebo.dingtalk.robot.message.MarkdownMessage;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * @author yibo
 * @since 2020-08-08
 */
public class DingtalkRobotClientTest {

    @Test(expected = InvalidKeyException.class)
    public void send() {
        IMessage message = new MarkdownMessage(
                new MarkdownMessage.Markdown(
                        "杭州天气",
                        "#### 杭州天气 @150XXXXXXXX \n> 9度，西北风1级，空气良89，相对温度73%\n> ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n> ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"
                ),
                new At(new ArrayList<String>() {{
                    add("150XXXXXXXX");
                }})
        );
        boolean success = DingtalkRobotClients.get(DingtalkRobotInfo.ALERT_ROBOT).send(message);
        assertTrue(success);
    }

}