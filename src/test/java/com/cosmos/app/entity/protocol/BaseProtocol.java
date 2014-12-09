package com.cosmos.app.entity.protocol;

import com.cosmos.protocol.common.CommonPb;

/**
 * Protocol超类
 *
 * @author David
 */
public class BaseProtocol {

    /**
     * 构建id/name的Protocol Buffer结构
     *
     * @param id   id
     * @param name 名称
     * @return id/name的Protocol Buffer结构
     */
    public static CommonPb.IdAndName.Builder builderIdAndName(long id, String name) {
        return CommonPb.IdAndName.newBuilder().setId((int) id).setName(name);
    }
}
