package com.superapp.redsocial.notification.infraestructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.superapp.redsocial.notification.domain.utils.SALogger;

import java.util.List;

public abstract class SQSHandler implements  RequestHandler<SQSEvent, String> {
    protected abstract void preHandler();

    protected abstract void handleRequest(List<SQSMessage> request, Context context);

    protected abstract void postHandler();

    /**
     * Event type SQS
     * @param input
     * @param context
     * @return A string OK
     */
    @Override
    public String handleRequest(SQSEvent input, Context context) {
        context.getLogger();
        SALogger.info(this.getClass().getName(),"handleRequest","SQSHandler");
        preHandler();

        handleRequest(input.getRecords(), context);

        try {
            postHandler();
        } catch (Exception e) {
            SALogger.error(this.getClass().getName(), "handleRequest", e.getMessage());
        }
        return "OK";
    }
}
