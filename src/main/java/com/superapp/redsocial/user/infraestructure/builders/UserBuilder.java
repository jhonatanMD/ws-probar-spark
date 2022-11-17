package com.superapp.redsocial.user.infraestructure.builders;

import com.superapp.redsocial.core.shared.utils.Strings;
import com.superapp.redsocial.core.shared.utils.UCollections;
import com.superapp.redsocial.user.domain.entity.TaggedUser;
import com.superapp.redsocial.user.domain.entity.User;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.Map;

import static com.superapp.redsocial.core.shared.utils.UCollections.extStr;

public class UserBuilder {
    public static User fromFoldMap(Map<?, ?> userData) {

        /*------------------- ⚠ HARD CODE ⚠ -----------------*/
        // Will be removed when implement configuration system
        String name = Strings.capWords(String.format("%s %s", extStr(userData, "name", ""), extStr(userData, "lastName", ""))).trim();
        if (UCollections.extInt(userData, "type", 0) == 2) {
            name = String.format("%s %s", extStr(userData, "name", ""), extStr(userData, "lastName", "")).trim();
        }
        /*------------------- ⚠ HARD CODE ⚠ -----------------*/

        return new User(
                UCollections.extStr(userData, T.id),
                name,
                UCollections.extStr(userData, "thumbnail", null),
                UCollections.extStr(userData, "image", null),
                UCollections.extInt(userData, "type", 1)
        );
    }

    /**
     * Build tagged user from raw data
     *
     * @param userData Raw data
     *
     * @return TaggedUser instance
     */
    public static TaggedUser buildTaggedUser(Map<?, ?> userData) {
        return new TaggedUser(
                UCollections.extStr(userData, T.id),
                UCollections.extInt(userData, "type", 1)
        );
    }

    public static String buildHashtags(Map<?, ?> hashtagData) {
        return UCollections.extStr(hashtagData, T.id);
    }

}
