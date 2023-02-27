package org.web.service;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.web.common.ValidationException;
import org.web.entity.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

class UserServiceTest {

  UserService userService = new UserService();
  DynamoDbClient dynamoDbClient = Mockito.mock(DynamoDbClient.class);

  @BeforeEach
  void beforeEach() {
    userService.dynamoDbClient = dynamoDbClient;
  }

  @Test
  void test_save_whenSucceed() {
    Mockito.when(dynamoDbClient.putItem(Mockito.any(PutItemRequest.class))).thenReturn(null);
    var user = new User("", "", "email", "");

    userService.save(user);
    Assertions.assertNotNull(user.getId());
    Assertions.assertNotNull(user.getCreatedAt());
    Assertions.assertNotNull(user.getUpdatedAt());
  }

  @Test
  void test_save_whenThrowException() {
    Mockito.when(dynamoDbClient.putItem(Mockito.any(PutItemRequest.class)))
        .thenThrow(Mockito.mock(RuntimeException.class));
    var user = new User("user", "pass", "", "");

    Assertions.assertThrows(ValidationException.class, () -> userService.save(user));
  }

  @Test
  void test_getUser() {
    GetItemResponse getItemResponse = Mockito.mock(GetItemResponse.class);
    Mockito.when(dynamoDbClient.getItem(Mockito.any(GetItemRequest.class)))
        .thenReturn(getItemResponse);
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("username", AttributeValue.fromS("user1"));
    Mockito.when(getItemResponse.item()).thenReturn(item);

    final User response = userService.getUser("user1");
    Assertions.assertNotNull(response);
    Assertions.assertEquals("user1", response.getUsername());
    Assertions.assertNull(response.getId());
  }
}
