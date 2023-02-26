package org.web.service;

import org.web.common.DateCommonUtil;
import org.web.common.ValidationException;
import org.web.entity.Answer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class AnswerService {
  @Inject
  DynamoDbClient dynamoDbClient;

  public Answer save(Answer answer) {
    answer.setId(UUID.randomUUID().toString());
    answer.setCreatedAt(new Date());
    answer.setUpdatedAt(new Date());

    try {
      dynamoDbClient.putItem(putRequest(answer));
    } catch (Exception e) {
      throw new ValidationException("system_error");
    }
    return answer;
  }

  public List<Answer> getAnswersByUser(String username) {
    var key = new HashMap<String, AttributeValue>();
    key.put("username", AttributeValue.fromS(username));

    Map<String, AttributeValue> exprAttrValues = new HashMap<>();
    exprAttrValues.put(":username", AttributeValue.fromS(username));
    QueryResponse queryResponse = dynamoDbClient.query(
        QueryRequest.builder().keyConditionExpression("username = :username")
            .expressionAttributeValues(exprAttrValues)
            .tableName(getTableName())
            .build()
    );
    if (queryResponse.hasItems()) {
      return queryResponse.items().stream().map(Answer::from).collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }

  }

  private PutItemRequest putRequest(Answer answer) {
    var item = new HashMap<String, AttributeValue>();
    item.put("id", AttributeValue.fromS(answer.getId()));
    item.put("username", AttributeValue.fromS(answer.getUsername()));
    item.put("createdAt", AttributeValue.builder().s(DateCommonUtil.dateToString(answer.getCreatedAt())).build());
    item.put("updatedAt", AttributeValue.builder().s(DateCommonUtil.dateToString(answer.getUpdatedAt())).build());
    item.put("questionNo", AttributeValue.fromN(String.valueOf(answer.getQuestionNo())));
    item.put("point", AttributeValue.fromN(String.valueOf(answer.getPoint())));
    return PutItemRequest.builder().tableName(getTableName()).item(item)
        .build();
  }

  private String getTableName() {
    return "driver-sim-man_answer";
  }
}
