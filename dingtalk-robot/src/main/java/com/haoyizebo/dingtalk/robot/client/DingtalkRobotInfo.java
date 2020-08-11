package com.haoyizebo.dingtalk.robot.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yibo
 * @since 2020-08-08
 */
@AllArgsConstructor
@Getter
public enum DingtalkRobotInfo {
    /**
     * 报警机器人
     */
    ALERT_ROBOT("your_access_token", "your_secret"),
    ;

    /**
     * webhook 的 access_token
     */
    private final String accessToken;
    /**
     * 采用加签的安全模式获取到的 secret
     */
    private final String secret;

}
