package org.web.service;

import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.web.entity.Answer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Disabled
class AnswerServiceIntegrationTest {

  DynamoDbClient dynamoDbClient;
  AnswerService answerService;

  @BeforeEach
  void beforeEach() {
    dynamoDbClient =
        DynamoDbClient.builder()
            .endpointOverride(URI.create("http://localhost:8000"))
            .region(Region.AP_SOUTHEAST_1)
            .build();
    answerService = new AnswerService();
    answerService.dynamoDbClient = dynamoDbClient;
  }

  @Test
  void test_save() {
    Answer answer = new Answer(null, "lampv", 2, 4);
    answerService.save(answer);

    var response = answerService.getAnswersByUser("lampv");
    Assertions.assertNotNull(response);
  }
}
