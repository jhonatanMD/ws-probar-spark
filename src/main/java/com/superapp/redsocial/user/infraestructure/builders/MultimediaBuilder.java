package com.superapp.redsocial.user.infraestructure.builders;

import com.superapp.redsocial.core.shared.utils.UCollections;
import com.superapp.redsocial.user.domain.constants.AudioValues;
import com.superapp.redsocial.user.domain.entity.Multimedia;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.Map;


public final class MultimediaBuilder {

    public static Multimedia fromFoldMapObject(final Object map) {
        final Map<?, ?> multimediaData = (Map<?, ?>) map;
        final var height = UCollections.extInt(multimediaData, "height", -1);
        final var width = UCollections.extInt(multimediaData, "width", -1);
        final var type=UCollections.extStr(multimediaData,"mimeType",null);
        var audio=UCollections.extInt(multimediaData,"audio", AudioValues.WITH_AUDIO.V());
        if(type.equals("image/jpeg")){
            audio= AudioValues.IMAGE.V();
        }
        return new Multimedia(
                UCollections.extStr(multimediaData, T.id),
                UCollections.extStr(multimediaData, "url"),
                UCollections.extStr(multimediaData, "thumbnail", null),
                UCollections.extStr(multimediaData, "mimeType", null),
                height == -1 ? null : height,
                width == -1 ? null : width,
                audio,
                UCollections.extInt(multimediaData,"version",1)
        );
    }
}
