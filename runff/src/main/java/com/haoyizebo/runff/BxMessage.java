package com.haoyizebo.runff;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @author yibo
 * @since 2020-08-17
 */
@Data
@JacksonXmlRootElement(localName = "BxMessage")
public class BxMessage {

    @JacksonXmlProperty(localName = "AppId")
    private String appId;
    @JacksonXmlProperty(localName = "Type")
    private int type;
    @JacksonXmlProperty(localName = "Action")
    private String action;
    /**
     * ==2 请登录
     */
    @JacksonXmlProperty(localName = "StateCode")
    private int stateCode;
    @JacksonXmlProperty(localName = "Message")
    private String message;
    @JacksonXmlProperty(localName = "Data")
    private Data data;

    @lombok.Data
    public static class Data {
        @JacksonXmlProperty(localName = "list")
        private String list;
        @JacksonXmlProperty(localName = "more")
        private boolean more;
        @JacksonXmlProperty(localName = "total")
        private int total;

        public Data(String _ignore) {
        }

        public Data() {
        }

    }

}
