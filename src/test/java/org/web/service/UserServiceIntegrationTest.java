package org.web.service;

import io.quarkus.test.Mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web.entity.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

class UserServiceIntegrationTest {

  @Mock DynamoDbClient dynamoDbClient;
  UserService userService;

  @BeforeEach
  void beforeEach() {
    //    dynamoDbClient =
    //        DynamoDbClient.builder()
    //            .endpointOverride(URI.create("http://localhost:8000"))
    //            .region(Region.AP_SOUTHEAST_1)
    //            .build();
    //    userService = new UserService();
    //    userService.dynamoDbClient = dynamoDbClient;
  }

  @Test
  void test_save_getUser() {
    User user = new User("lampv", "123456", "lampv@gmail.com", null);
    userService.save(user);

    User foundUser = userService.getUser("lampv");
    Assertions.assertNotNull(foundUser.getId());

    User loginResult = userService.login("lampv", "123456");
    Assertions.assertNotNull(loginResult.getId());
  }
}
