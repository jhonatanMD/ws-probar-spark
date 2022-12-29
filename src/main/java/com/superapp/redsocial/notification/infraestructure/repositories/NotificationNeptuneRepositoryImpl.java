package com.superapp.redsocial.notification.infraestructure.repositories;

import com.superapp.core.neptune.application.redsocial.RsClusterManager;
import com.superapp.redsocial.core.shared.utils.Strings;
import com.superapp.redsocial.notification.domain.entity.Notificacion;
import com.superapp.redsocial.notification.domain.entity.Union;
import com.superapp.redsocial.notification.domain.entity.Usuario;
import com.superapp.redsocial.notification.domain.events.RequestData;
import com.superapp.redsocial.notification.domain.repositories.NotificationNeptuneRepository;
import com.superapp.redsocial.notification.domain.utils.Status;
import com.superapp.redsocial.notification.domain.utils.SubType;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.superapp.redsocial.core.shared.utils.UCollections.extStr;
import static com.superapp.redsocial.notification.domain.utils.Type.FRIEND_REQUEST;

public class NotificationNeptuneRepositoryImpl implements NotificationNeptuneRepository {

    @Override
    public Union getDataRelationship(RequestData requestData, String message) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            final var d = g.V().as("receptor").inE().
                    hasId(requestData.getTarget()).as("r").otherV().
                    as("emisor").select("receptor", "r", "emisor").by(__.valueMap().
                    with(WithOptions.tokens));
            var dataRelationship = d.next();

            if(requestData.getType() == SubType.SENT_FRIEND_REQUEST.value()){ //El que manda la notificación


                Usuario usuario = new Usuario(
                        Strings.capWords(extStr((Map<?, ?>) dataRelationship.get("emisor"), "name") + " " + extStr((Map<?, ?>) dataRelationship.get("emisor"), "lastName")),
                        extStr((Map<?, ?>) dataRelationship.get("emisor"), T.id),
                        extStr((Map<?, ?>) dataRelationship.get("emisor"), "image"));

                var userTo = extStr((Map<?, ?>) dataRelationship.get("receptor"), T.id);
                var sicu = extStr((Map<?, ?>) dataRelationship.get("receptor"), "sicu");
                var name = extStr((Map<?, ?>) dataRelationship.get("receptor"), "name");
                var idNotification = UUID.randomUUID();
                List<String> list = new ArrayList<>();
                list.add(usuario.getId());

                Notificacion notificacion = new Notificacion(
                        idNotification, null, requestData.getTarget(), usuario.getName() + message,
                        null, FRIEND_REQUEST.value(), requestData.getType(), Status.SENT.value(), requestData.getDate(),
                        0, usuario, list);

                Union union = new Union(userTo, notificacion, sicu, Strings.capWords(name));

                return union;

            } if(requestData.getType() == SubType.ACCEPTED_FRIEND_REQUEST.value()){ //El que acepta la notificación
                Usuario usuario = new Usuario(
                        Strings.capWords(extStr((Map<?, ?>) dataRelationship.get("receptor"), "name")+ " " + extStr((Map<?, ?>) dataRelationship.get("receptor"), "lastName")),
                        extStr((Map<?, ?>) dataRelationship.get("receptor"), T.id),
                        extStr((Map<?, ?>) dataRelationship.get("receptor"), "image"));

                var userTo = extStr((Map<?, ?>) dataRelationship.get("emisor"), T.id);
                var sicuEmisor = extStr((Map<?, ?>) dataRelationship.get("emisor"), "sicu");
                var name = extStr((Map<?, ?>) dataRelationship.get("emisor"), "name");
                var idNotification = UUID.randomUUID();
                List<String> list = new ArrayList<>();
                list.add(usuario.getId());

                Notificacion notificacion = new Notificacion(
                        idNotification, null, requestData.getTarget(), usuario.getName() + message, null,
                        FRIEND_REQUEST.value(), requestData.getType(), Status.SENT.value(), requestData.getDate(),
                        0, usuario, list);

                Union union = new Union(userTo, notificacion, sicuEmisor, Strings.capWords(name));

                return union;
            }

            return null;

        }finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }
}
