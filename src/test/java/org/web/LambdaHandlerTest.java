package org.web;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.web.entity.User;
import org.web.service.AnswerService;
import org.web.service.UserService;

import java.util.HashMap;

class LambdaHandlerTest {

  LambdaHandler lambdaHandler;
  UserService userService;
  AnswerService answerService;
  Context context;
  ObjectMapper objectMapper;

  @BeforeEach
  void beforeEach() {
    lambdaHandler = new LambdaHandler();
    userService = Mockito.mock(UserService.class);
    answerService = Mockito.mock(AnswerService.class);
    objectMapper = new ObjectMapper();
    lambdaHandler.userService = userService;
    lambdaHandler.answerService = answerService;
    lambdaHandler.objectMapper = objectMapper;

    context = Mockito.mock(Context.class);
    LambdaLogger lambdaLogger = Mockito.mock(LambdaLogger.class);
    Mockito.when(context.getLogger()).thenReturn(lambdaLogger);
  }

  @Test
  void test_postUser() {
    User mockUser = new User();
    var event = new HashMap<String, Object>();
    event.put("path", "/users");
    event.put("httpMethod", "POST");
    event.put("body", "{\"username\":\"lampv2\",\"password\":\"123456\",\"email\":\"lampv2@gmail.com\"}");
    Mockito.when(userService.save(Mockito.any(User.class))).thenReturn(mockUser);

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(201, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }
}