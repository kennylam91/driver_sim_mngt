package org.web;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.web.common.ValidationException;
import org.web.entity.Answer;
import org.web.entity.User;
import org.web.service.AnswerService;
import org.web.service.UserService;

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
  void test_postUser_whenSucceed() throws JsonProcessingException {
    HashMap<String, Object> event = createEvent("/users", "POST");
    event.put("body", "{\"username\":\"lampv2\",\"password\":\"123456\",\"email\":\"\"}");
    User mockUser = new User("username", "pass", "", "");
    Mockito.when(userService.save(Mockito.any(User.class))).thenReturn(mockUser);

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(201, response.getStatusCode());
    Assertions.assertEquals(objectMapper.writeValueAsString(mockUser), response.getBody());
  }

  @Test
  void test_postUser_whenFail() {
    HashMap<String, Object> event = createEvent("/users", "POST");
    event.put("body", "{\"username\":\"lampv2\",\"password\":\"123456\",\"email\":\"\"}");
    User mockUser = new User("username", "pass", "", "");
    Mockito.when(userService.save(Mockito.any(User.class)))
        .thenThrow(new ValidationException("username_existed"));

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(400, response.getStatusCode());
    Assertions.assertEquals("username_existed", response.getBody());
  }

  @Test
  void test_getUser_whenSucceed() throws JsonProcessingException {
    var event = createEvent("/users/user_1", "GET");

    User mockUser = new User("user_1", "pass", "", "");
    Mockito.when(userService.getUser("user_1")).thenReturn(mockUser);

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(200, response.getStatusCode());
    Assertions.assertEquals(objectMapper.writeValueAsString(mockUser), response.getBody());
  }

  @Test
  void test_getUser_whenFail() {
    var event = createEvent("/users/user_1", "GET");

    User mockUser = new User("user_1", "pass", "", "");
    Mockito.when(userService.getUser("user_1")).thenReturn(null);

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(404, response.getStatusCode());
  }

  @Test
  void test_postLogin_whenSucceed() throws JsonProcessingException {
    var event = createEvent("/login", "POST");
    event.put("body", "{\"username\":\"username\",\"password\":\"password\" }");

    User mockUser = new User("username", "password", "email", "phone");
    Mockito.when(userService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(mockUser);

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(200, response.getStatusCode());
    Assertions.assertEquals(objectMapper.writeValueAsString(mockUser), response.getBody());
  }

  @Test
  void test_postLogin_whenFail() {
    var event = createEvent("/login", "POST");
    event.put("body", "{\"username\":\"username\",\"password\":\"password\" }");

    User mockUser = new User("username", "password", "email", "phone");
    Mockito.when(userService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(400, response.getStatusCode());
    Assertions.assertEquals("username_password_not_correct", response.getBody());
  }

  @Test
  void test_postAnswer_whenSucceed() throws JsonProcessingException {
    var event = createEvent("/answers", "POST");
    event.put("body", "{\"username\":\"username\",\"questionNo\":1,\"point\":5}");

    var mockAnswer = new Answer("id", "username", 1, 5);
    Mockito.when(answerService.save(Mockito.any(Answer.class))).thenReturn(mockAnswer);

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(201, response.getStatusCode());
    Assertions.assertEquals(objectMapper.writeValueAsString(mockAnswer), response.getBody());
  }

  @Test
  void test_postAnswer_whenFail() throws JsonProcessingException {
    var event = createEvent("/answers", "POST");
    event.put("body", "{\"username\":\"username\",\"questionNo\":1,\"point\":5}");

    var mockAnswer = new Answer("id", "username", 1, 5);
    Mockito.when(answerService.save(Mockito.any(Answer.class)))
        .thenThrow(new ValidationException("error_happened"));

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(400, response.getStatusCode());
    Assertions.assertEquals("error_happened", response.getBody());
  }

  @Test
  void test_getAnswer_whenSucceed() throws JsonProcessingException {
    var event = createEvent("/answers/user_1", "GET");

    Answer mockAnswer = new Answer("id", "user_1", 1, 5);
    Mockito.when(answerService.getAnswersByUser("user_1")).thenReturn(List.of(mockAnswer));

    var response = lambdaHandler.handleRequest(event, context);
    Assertions.assertEquals(200, response.getStatusCode());
    Assertions.assertEquals(
        objectMapper.writeValueAsString(List.of(mockAnswer)), response.getBody());
  }

  @Test
  void test_invalidPath() {
    List.of(
            createEvent("/invalid-path", "GET"),
            createEvent("/users", "GET"),
            createEvent("/answers", "GET"))
        .forEach(
            event -> {
              var response = lambdaHandler.handleRequest(event, context);
              Assertions.assertEquals(404, response.getStatusCode());
            });
  }

  private HashMap<String, Object> createEvent(String path, String httpMethod) {
    var event = new HashMap<String, Object>();
    event.put("path", path);
    event.put("httpMethod", httpMethod);
    return event;
  }
}
