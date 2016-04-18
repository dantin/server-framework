package com.cosmos.server.commons.utils;

import com.cosmos.server.commons.exceptions.SerializeException;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.nio.charset.Charset;

public class SerializeUtils {

    private static final Charset CHARSET = Charsets.UTF_8;

    private static final Gson GSON = new GsonBuilder().create();

    public static byte[] encode(Object stuff) {
        return GSON.toJson(stuff).getBytes(CHARSET);
    }

    public static Object decode(String stuff, Class<?> clazz) throws SerializeException {
        try {
            return GSON.fromJson(stuff, clazz);
        } catch (JsonSyntaxException e) {
            throw new SerializeException(String.format("%s decode exception", stuff));
        }
    }
}
