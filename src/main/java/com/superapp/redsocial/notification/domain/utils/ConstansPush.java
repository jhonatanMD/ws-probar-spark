package com.superapp.redsocial.notification.domain.utils;

import com.superapp.redsocial.core.shared.utils.Env;

public enum ConstansPush {

    ID_PLANTILLA_AMISTAD("SA_RRSS_SOLICITUD_AMISTAD"),
    ID_PLANTILLA_AMISTAD_ACEPTADA("SA_RRSS_NOTIFICACION_SOLICITUD_ACEPTADA"),
    X_ID_INTEREACCION(Env.var("IdInteraccion").get()),
    FLOW("GSIFSn_Not");

    private final String value;

    ConstansPush(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }


}
