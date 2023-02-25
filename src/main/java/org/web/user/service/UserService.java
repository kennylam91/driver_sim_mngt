package org.web.user.service;

import org.web.common.DateCommonUtil;
import org.web.user.entity.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@ApplicationScoped
public class UserService {
  private static final String DYNAMODB_USER_TABLE_NAME = "driver_simulation_user";

  @Inject
  DynamoDbClient dynamoDbClient;

  public UserService() {

  }

  public User save(User user) {
    user.setId(UUID.randomUUID().toString());
    user.setCreatedAt(new Date());
    user.setUpdatedAt(new Date());

    dynamoDbClient.putItem(putRequest(user));
    return user;
  }

  public User getUser(String id) {
    return User.from(dynamoDbClient.getItem(getRequest(id)).item());
  }

  private String getTableName() {
    return "driver-sim-man_user";
  }

  private PutItemRequest putRequest(User user) {
    var item = new HashMap<String, AttributeValue>();
    item.put("id", AttributeValue.fromS(user.getId()));
    item.put("username", AttributeValue.builder().s(user.getUsername()).build());
    item.put("password", AttributeValue.builder().s(user.getPassword()).build());
    item.put("email", AttributeValue.builder().s(user.getEmail()).build());
    item.put("phone", AttributeValue.builder().s(user.getPhone()).build());
    item.put("createdAt", AttributeValue.builder().s(DateCommonUtil.dateToString(user.getCreatedAt())).build());
    item.put("updatedAt", AttributeValue.builder().s(DateCommonUtil.dateToString(user.getUpdatedAt())).build());

    return PutItemRequest.builder().tableName(getTableName()).item(item).build();
  }

  private GetItemRequest getRequest(String id) {
    var key = new HashMap<String, AttributeValue>();
    key.put("id", AttributeValue.fromS(id));

    return GetItemRequest.builder().tableName(getTableName()).key(key)
        .attributesToGet("id", "username", "email", "phone", "createdAt", "updatedAt").build();
  }
}
