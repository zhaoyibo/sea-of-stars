package com.haoyizebo.dingtalk.robot.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * @author yibo
 * @since 2020-08-07
 */
public class MessageTest {

    Logger logger = LoggerFactory.getLogger(MessageTest.class);
    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    @Test
    public void testTextMessage() throws JsonProcessingException {
        String expected = "{\n" +
                "    \"msgtype\": \"text\", \n" +
                "    \"text\": {\n" +
                "        \"content\": \"我就是我, 是不一样的烟火  \\n  @156xxxx8827 @189xxxx8325 \"\n" +
                "    }, \n" +
                "    \"at\": {\n" +
                "        \"atMobiles\": [\n" +
                "            \"156xxxx8827\", \n" +
                "            \"189xxxx8325\"\n" +
                "        ], \n" +
                "        \"isAtAll\": false\n" +
                "    }\n" +
                "}";
        IMessage message = new TextMessage(
                "我就是我, 是不一样的烟火",
                new At(new ArrayList<String>() {{
                    add("156xxxx8827");
                    add("189xxxx8325");
                }})
        );
        assertJson(message, expected);

        expected = "{\n" +
                "    \"msgtype\": \"text\", \n" +
                "    \"text\": {\n" +
                "        \"content\": \"我就是我, 是不一样的烟火\"\n" +
                "    }, \n" +
                "    \"at\": {\n" +
                "        \"atMobiles\": [\n" +
                "            \"156xxxx8827\", \n" +
                "            \"189xxxx8325\"\n" +
                "        ], \n" +
                "        \"isAtAll\": false\n" +
                "    }\n" +
                "}";

        message = new TextMessage(
                "我就是我, 是不一样的烟火",
                new At(new ArrayList<String>() {{
                    add("156xxxx8827");
                    add("189xxxx8325");
                }}).setAutoAt(false)
        );
        assertJson(message, expected);

        expected = "{\n" +
                "    \"msgtype\": \"text\", \n" +
                "    \"text\": {\n" +
                "        \"content\": \"我就是我, 是不一样的烟火\"\n" +
                "    }, \n" +
                "    \"at\": {\n" +
                "        \"isAtAll\": true\n" +
                "    }\n" +
                "}";

        message = new TextMessage(
                "我就是我, 是不一样的烟火",
                new At(true).setAutoAt(false)
        );
        assertJson(message, expected);

        expected = "{\n" +
                "    \"msgtype\": \"text\", \n" +
                "    \"text\": {\n" +
                "        \"content\": \"我就是我, 是不一样的烟火  \\n  @所有人\"\n" +
                "    }, \n" +
                "    \"at\": {\n" +
                "        \"isAtAll\": true\n" +
                "    }\n" +
                "}";

        message = new TextMessage(
                "我就是我, 是不一样的烟火",
                new At(true)
        );
        assertJson(message, expected);
    }

    @Test
    public void testLinkMessage() throws JsonProcessingException {
        String expected = "{\n" +
                "    \"msgtype\": \"link\", \n" +
                "    \"link\": {\n" +
                "        \"text\": \"这个即将发布的新版本，创始人xx称它为红树林。而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是红树林\", \n" +
                "        \"title\": \"时代的火车向前开\", \n" +
                "        \"picUrl\": \"\", \n" +
                "        \"messageUrl\": \"https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI\"\n" +
                "    }\n" +
                "}";
        IMessage message = new LinkMessage(
                new LinkMessage.Link(
                        "时代的火车向前开",
                        "这个即将发布的新版本，创始人xx称它为红树林。而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是红树林",
                        "https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI",
                        ""
                )
        );
        assertJson(message, expected);
    }

    @Test
    public void testMarkdownMessage() throws JsonProcessingException {
        String expected = "{\n" +
                "     \"msgtype\": \"markdown\",\n" +
                "     \"markdown\": {\n" +
                "         \"title\":\"杭州天气\",\n" +
                "         \"text\": \"#### 杭州天气 @150XXXXXXXX \\n> 9度，西北风1级，空气良89，相对温度73%\\n> ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\\n> ###### 10点20分发布 [天气](https://www.dingtalk.com) \\n  \\n  @150XXXXXXXX \"\n" +
                "     },\n" +
                "      \"at\": {\n" +
                "          \"atMobiles\": [\n" +
                "              \"150XXXXXXXX\"\n" +
                "          ],\n" +
                "          \"isAtAll\": false\n" +
                "      }\n" +
                " }\n";
        IMessage message = new MarkdownMessage(
                new MarkdownMessage.Markdown(
                        "杭州天气",
                        "#### 杭州天气 @150XXXXXXXX \n> 9度，西北风1级，空气良89，相对温度73%\n> ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n> ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"
                ),
                new At(new ArrayList<String>() {{
                    add("150XXXXXXXX");
                }})
        );
        assertJson(message, expected);
    }

    @Test
    public void testActionCardMessage() throws JsonProcessingException {
        String expected = "{\n" +
                "    \"actionCard\": {\n" +
                "        \"title\": \"乔布斯 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身\", \n" +
                "        \"text\": \"![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png) \n" +
                " ### 乔布斯 20 年前想打造的苹果咖啡厅 \n" +
                " Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划\", \n" +
                "        \"btnOrientation\": \"0\", \n" +
                "        \"singleTitle\" : \"阅读全文\",\n" +
                "        \"singleURL\" : \"https://www.dingtalk.com/\"\n" +
                "    }, \n" +
                "    \"msgtype\": \"actionCard\"\n" +
                "}";
        IMessage message = new ActionCardMessage(
                new ActionCardMessage.ActionCard(
                        "乔布斯 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身",
                        "![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png) \n" +
                                " ### 乔布斯 20 年前想打造的苹果咖啡厅 \n" +
                                " Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划"
                )
                        .setSingleTitle("阅读全文")
                        .setSingleURL("https://www.dingtalk.com/")
                        .setBtnOrientation("0")
        );
        assertJson(message, expected);

        expected = "{\n" +
                "    \"actionCard\": {\n" +
                "        \"title\": \"乔布斯 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身\", \n" +
                "        \"text\": \"![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png) \n" +
                " ### 乔布斯 20 年前想打造的苹果咖啡厅 \n" +
                " Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划\", \n" +
                "        \"btnOrientation\": \"0\", \n" +
                "        \"btns\": [\n" +
                "            {\n" +
                "                \"title\": \"内容不错\", \n" +
                "                \"actionURL\": \"https://www.dingtalk.com/\"\n" +
                "            }, \n" +
                "            {\n" +
                "                \"title\": \"不感兴趣\", \n" +
                "                \"actionURL\": \"https://www.dingtalk.com/\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }, \n" +
                "    \"msgtype\": \"actionCard\"\n" +
                "}";
        message = new ActionCardMessage(
                new ActionCardMessage.ActionCard(
                        "乔布斯 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身",
                        "![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png) \n" +
                                " ### 乔布斯 20 年前想打造的苹果咖啡厅 \n" +
                                " Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划"
                )
                        .setBtns(new ArrayList<ActionCardMessage.Btn>() {{
                            add(new ActionCardMessage.Btn("内容不错", "https://www.dingtalk.com/"));
                            add(new ActionCardMessage.Btn("不感兴趣", "https://www.dingtalk.com/"));
                        }})
                        .setBtnOrientation("0")
        );
        assertJson(message, expected);
    }

    @Test
    public void testFeedCardMessage() throws JsonProcessingException {
        String expected = "{\n" +
                "    \"feedCard\": {\n" +
                "        \"links\": [\n" +
                "            {\n" +
                "                \"title\": \"时代的火车向前开\", \n" +
                "                \"messageURL\": \"https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI\", \n" +
                "                \"picURL\": \"https://gw.alicdn.com/tfs/TB1ayl9mpYqK1RjSZLeXXbXppXa-170-62.png\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"title\": \"时代的火车向前开2\", \n" +
                "                \"messageURL\": \"https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI\", \n" +
                "                \"picURL\": \"https://gw.alicdn.com/tfs/TB1ayl9mpYqK1RjSZLeXXbXppXa-170-62.png\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }, \n" +
                "    \"msgtype\": \"feedCard\"\n" +
                "}";
        FeedCardMessage message = new FeedCardMessage(
                new ArrayList<FeedCardMessage.Link>() {{
                    add(new FeedCardMessage.Link(
                            "时代的火车向前开",
                            "https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI",
                            "https://gw.alicdn.com/tfs/TB1ayl9mpYqK1RjSZLeXXbXppXa-170-62.png"
                    ));
                    add(new FeedCardMessage.Link(
                            "时代的火车向前开2",
                            "https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI",
                            "https://gw.alicdn.com/tfs/TB1ayl9mpYqK1RjSZLeXXbXppXa-170-62.png"
                    ));
                }}
        );
        assertJson(message, expected);
    }

    private void assertJson(IMessage message, String json) throws JsonProcessingException {
        JsonNode actual = objectMapper.readTree(objectMapper.writeValueAsString(message));
        JsonNode expected = objectMapper.readTree(json);
        assertEquals(expected, actual);
    }


}