package com.haoyizebo.dingtalk.robot.client;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yibo
 * @since 2020-08-08
 */
public class DingtalkRobotClients {

    private static final ConcurrentHashMap<String, DingtalkRobotClient> CACHE = new ConcurrentHashMap<>();

    public static DingtalkRobotClient get(DingtalkRobotInfo dingtalkRobotInfo) {
        return get(dingtalkRobotInfo.getAccessToken(), dingtalkRobotInfo.getSecret());
    }

    public static DingtalkRobotClient get(String accessToken, String secret) {
        return CACHE.computeIfAbsent(accessToken + "-" + secret, (k) -> new DingtalkRobotClient(accessToken, secret));
    }

}
