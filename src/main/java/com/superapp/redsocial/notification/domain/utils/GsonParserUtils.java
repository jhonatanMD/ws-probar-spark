package com.superapp.redsocial.notification.domain.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonParserUtils {
    private static Gson gson = null;

    public GsonParserUtils() {
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = (new GsonBuilder()).serializeNulls().create();
        }

        return gson;
    }
}
