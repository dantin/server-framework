package com.cosmos.netty;

/**
 * 支持的协议
 *
 * @author David
 */
public enum Protocol {

    /**
     * Unsupported Protocol
     */
    UNKNOWN("unknown"),
    /**
     * Google Protocol Buffer
     */
    PROTOBUF("protobuf");

    private String code;

    private Protocol(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
