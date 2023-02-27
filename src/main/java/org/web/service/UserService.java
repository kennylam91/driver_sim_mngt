package org.web.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.web.common.DateCommonUtil;
import org.web.common.ValidationException;
import org.web.entity.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.utils.StringUtils;

@ApplicationScoped
public class UserService {
  @Inject DynamoDbClient dynamoDbClient;

  public UserService() {}

  public User save(User user) {
    validateUser(user);

    user.setId(UUID.randomUUID().toString());
    user.setCreatedAt(new Date());
    user.setUpdatedAt(new Date());

    try {
      dynamoDbClient.putItem(putRequest(user));
    } catch (ConditionalCheckFailedException e) {
      throw new ValidationException("username_existed");
    } catch (Exception e) {
      throw new ValidationException("system_error");
    }
    return user;
  }

  private void validateUser(User user) {
    if (StringUtils.isBlank(user.getUsername())) {
      throw new ValidationException("username_must_not_be_blank");
    }
    if (StringUtils.isBlank(user.getPassword())) {
      throw new ValidationException("password_must_not_be_blank");
    }
  }

  public User getUser(String username) {
    return User.from(dynamoDbClient.getItem(getRequest(username)).item());
  }

  public User login(String username, String password) {
    var key = new HashMap<String, AttributeValue>();
    key.put("username", AttributeValue.fromS(username));

    GetItemRequest getItemRequest =
        GetItemRequest.builder()
            .tableName(getTableName())
            .key(key)
            .attributesToGet(
                "id", "username", "password", "email", "phone", "createdAt", "updatedAt")
            .build();
    Map<String, AttributeValue> item = dynamoDbClient.getItem(getItemRequest).item();
    if (StringUtils.equals(item.get("password").s(), password)) {
      return User.from(item);
    } else {
      return null;
    }
  }

  private String getTableName() {
    return "driver-sim-man_user";
  }

  private PutItemRequest putRequest(User user) {
    var item = new HashMap<String, AttributeValue>();
    item.put("id", AttributeValue.fromS(user.getId()));
    item.put("username", AttributeValue.builder().s(user.getUsername()).build());
    item.put("password", AttributeValue.builder().s(user.getPassword()).build());
    if (StringUtils.isNotBlank(user.getEmail()))
      item.put("email", AttributeValue.builder().s(user.getEmail()).build());
    if (StringUtils.isNotBlank(user.getPhone()))
      item.put("phone", AttributeValue.builder().s(user.getPhone()).build());
    item.put(
        "createdAt",
        AttributeValue.builder().s(DateCommonUtil.dateToString(user.getCreatedAt())).build());
    item.put(
        "updatedAt",
        AttributeValue.builder().s(DateCommonUtil.dateToString(user.getUpdatedAt())).build());

    return PutItemRequest.builder()
        .tableName(getTableName())
        .item(item)
        .conditionExpression("attribute_not_exists(username)")
        .build();
  }

  private GetItemRequest getRequest(String username) {
    var key = new HashMap<String, AttributeValue>();
    key.put("username", AttributeValue.fromS(username));

    return GetItemRequest.builder()
        .tableName(getTableName())
        .key(key)
        .attributesToGet("id", "username", "email", "phone", "createdAt", "updatedAt")
        .build();
  }
}
