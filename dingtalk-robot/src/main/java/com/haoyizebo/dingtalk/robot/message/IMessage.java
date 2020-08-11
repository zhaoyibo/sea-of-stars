package com.haoyizebo.dingtalk.robot.message;

/**
 * @author yibo
 * @since 2020-08-07
 */
public interface IMessage {

    /**
     * 消息类型
     *
     * @return 消息类型
     */
    String getMsgType();

    String toJson();

}
