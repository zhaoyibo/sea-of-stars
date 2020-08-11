package com.haoyizebo.hexo.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author yibo
 * @since 2020-08-11
 */
public class YamlUtilsTest {

    @Test
    public void process() {
        String content = "---\n" +
                "title: Spring Cloud（二十）：Gateway 动态路由（金丝雀发布/灰度发布）\n" +
                "date: '2019-01-20 18:23:23'\n" +
                "tags:\n" +
                "  - Spring Cloud\n" +
                "  - Microservices\n" +
                "  - Spring Cloud Gateway\n" +
                "  - Gateway\n" +
                "  - Canary Release\n" +
                "  - Dynamic routing\n" +
                "abbrlink: 1962f450\n" +
                "index_img: https://cdn.jsdelivr.net/gh/zhaoyibo/resource@gh-pages/img/1596965886285-thumbnail.png\n" +
                "---\n" +
                "\n" +
                "## 为什么需要动态路由？\n" +
                "之前说过 Gateway 的路由配置，常用的有两种方式：\n" +
                "* Fluent API\n" +
                "* 配置文件\n";


        String expected = "---\n" +
                "title: Spring Cloud（二十）：Gateway 动态路由（金丝雀发布/灰度发布）\n" +
                "date: '2019-01-20 18:23:23'\n" +
                "tags:\n" +
                "  - Spring Cloud\n" +
                "  - Microservices\n" +
                "  - Spring Cloud Gateway\n" +
                "  - Gateway\n" +
                "  - Canary Release\n" +
                "  - Dynamic routing\n" +
                "index_img: https://cdn.jsdelivr.net/gh/zhaoyibo/resource@gh-pages/img/1596965886285-thumbnail.png\n" +
                "hello: world\n" +
                "---\n" +
                "\n" +
                "## 为什么需要动态路由？\n" +
                "之前说过 Gateway 的路由配置，常用的有两种方式：\n" +
                "* Fluent API\n" +
                "* 配置文件\n";

        Assert.assertEquals(expected, YamlUtils.process(content, map -> {
            map.remove("abbrlink");
            map.put("hello", "world");
        }));

    }


}