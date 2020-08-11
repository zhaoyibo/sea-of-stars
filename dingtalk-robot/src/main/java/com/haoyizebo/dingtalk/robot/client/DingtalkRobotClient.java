package com.haoyizebo.dingtalk.robot.client;

import com.haoyizebo.dingtalk.robot.exception.DingtalkException;
import com.haoyizebo.dingtalk.robot.exception.InvalidKeyException;
import com.haoyizebo.dingtalk.robot.message.IMessage;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author yibo
 * @since 2020-08-07
 */
public class DingtalkRobotClient {

    private final String accessToken;
    private final String secret;

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .build();

    public DingtalkRobotClient(String accessToken, String secret) {
        if (accessToken == null || accessToken.length() < 64
                || secret == null || secret.length() < 67) {
            throw new InvalidKeyException("invalid dingtalk access_token or secret");
        }
        this.accessToken = accessToken;
        this.secret = secret;
    }

    public DingtalkRobotClient(DingtalkRobotInfo dingtalkRobotInfo) {
        this(dingtalkRobotInfo.getAccessToken(), dingtalkRobotInfo.getSecret());
    }

    public boolean send(IMessage message) {
        try {
            RequestBody body = RequestBody.create(message.toJson(), JSON);
            Request request = new Request.Builder()
                    .url(getRequestUrl())
                    .post(body)
                    .build();
            try (Response response = CLIENT.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (NoSuchAlgorithmException | java.security.InvalidKeyException | IOException e) {
            throw new DingtalkException(e);
        }
    }

    private String getRequestUrl() throws NoSuchAlgorithmException, java.security.InvalidKeyException, UnsupportedEncodingException {
        Long timestamp = System.currentTimeMillis();

        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

        String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), "UTF-8");

        return "https://oapi.dingtalk.com/robot/send?"
                + "access_token=" + accessToken
                + "&timestamp=" + timestamp + "&sign=" + sign;
    }

}
