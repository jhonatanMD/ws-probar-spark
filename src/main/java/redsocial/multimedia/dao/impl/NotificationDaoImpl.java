package redsocial.multimedia.dao.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.superapp.core.microservice.utils.SALogger;
import redsocial.multimedia.dao.NotificationDao;

import java.util.*;


public class NotificationDaoImpl implements NotificationDao {

    private AmazonDynamoDB amazonDynamoDB;
    private DynamoDB dynamoDB;
    private Table tableSocketNotification;

    public NotificationDaoImpl() {
        amazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        dynamoDB = new DynamoDB(amazonDynamoDB);
        tableSocketNotification = dynamoDB.getTable("superapp.redsocial.watermark.connections");
    }


    @Override
    public String addConnection(String connectionId, String multimediaId)  {

            Item item = new Item();
            item.withPrimaryKey("multimediaId", multimediaId);
            item.withList("connectionIds", Arrays.asList(connectionId));

            PutItemSpec putItemSpec = new PutItemSpec();
            putItemSpec.withItem(item);
            putItemSpec.withConditionExpression("attribute_not_exists(multimediaId)");
            try {
                tableSocketNotification.putItem(putItemSpec);
                return multimediaId;
            } catch (Exception ex) {
                if (ex.getMessage().contains("The conditional request failed")) {
                    return null;
                }
                SALogger.error(this.getClass().getName(), "addConnection", ex.getMessage());
                throw ex;
            }


    }

    @Override
    public List<String> getConnection(String multimediaId) {

          try {
            Item item = tableSocketNotification.getItem("multimediaId" , multimediaId);
            if (item == null) {
                return List.of();
            }
            List<String> connectionIds = item.getList("connectionIds");
            return (connectionIds == null || connectionIds.isEmpty()) ? List.of() : connectionIds;
        } catch (Exception ex) {

            throw ex;
        }
    }


    @Override
    public void updateConnection(String multimediaId , List<String> connectionIds)  {
        Map<String, String> updateNames = new HashMap<>();
        updateNames.put("#S", "connectionIds");

        Map<String, Object> updateValues = new HashMap<>();
        updateValues.put(":connectionIdValue", connectionIds);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec();
        updateItemSpec.withPrimaryKey("multimediaId", multimediaId);
        updateItemSpec.withUpdateExpression("SET #S = :connectionIdValue");
        updateItemSpec.withNameMap(updateNames);
        updateItemSpec.withValueMap(updateValues);
        try {
            tableSocketNotification.updateItem(updateItemSpec);
        } catch (Exception ex) {
            SALogger.error(this.getClass().getName(), "addConnection", ex.getMessage());
        }
    }

}
