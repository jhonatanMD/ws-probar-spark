package com.superapp.redsocial.user.domain.constants;

public enum EstadosUsuario {
    PUEDES_ENVIAR(0),
    SOLICITUD_ENVIADA(1),
    SOLCITUD_POR_ACEPTAR(2),
    SEGUIDORES(3),
    BLOQUEADO(4),
    TE_SIGUE(5);

    private int value;

    EstadosUsuario(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
