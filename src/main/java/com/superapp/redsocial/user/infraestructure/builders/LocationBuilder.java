package com.superapp.redsocial.user.infraestructure.builders;

import com.superapp.redsocial.core.shared.utils.UCollections;
import com.superapp.redsocial.user.domain.entity.Location;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.Map;


public class LocationBuilder {
    public static Location fromFoldMap(final Map<?, ?> locationData) {
        return new Location(UCollections.extStr(locationData, T.id),
                UCollections.extStr(locationData, "lat"),
                UCollections.extStr(locationData, "lng"),
                UCollections.extStr(locationData, "name"),
                UCollections.extStr(locationData, "address"),
                UCollections.extStr(locationData, "img"),
                UCollections.extStr(locationData, "idLocationMap"));
    }

    public static Location fromFoldMapObject(final Object data) {
        final Map<?, ?> locationData = (Map<?, ?>) data;
        return new Location(UCollections.extStr(locationData, T.id),
                UCollections.extStr(locationData, "lat"),
                UCollections.extStr(locationData, "lng"),
                UCollections.extStr(locationData, "name"),
                UCollections.extStr(locationData, "address",null),
                UCollections.extStr(locationData, "img",null),
                UCollections.extStr(locationData, "idLocationMap",null));
    }
}
