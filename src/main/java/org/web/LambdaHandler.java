package org.web;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.web.common.ValidationException;
import org.web.entity.Answer;
import org.web.entity.User;
import org.web.service.AnswerService;
import org.web.service.UserService;
import software.amazon.awssdk.utils.StringUtils;

public class LambdaHandler
    implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

  @Inject UserService userService;
  @Inject AnswerService answerService;
  @Inject ObjectMapper objectMapper;

  @SneakyThrows
  @Override
  public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> event, Context context) {
    LambdaLogger logger = context.getLogger();
    String path = (String) event.get("path");
    String httpMethod = (String) event.get("httpMethod");
    Object body = event.get("body");
    boolean isPostMethod = "POST".equals(httpMethod);
    boolean isGetMethod = "GET".equals(httpMethod);

    APIGatewayProxyResponseEvent response = getDefaultApiGatewayProxyResponseEvent();

    if (StringUtils.isNotBlank(path) && StringUtils.isNotBlank(httpMethod)) {
      if (path.startsWith("/users")) {
        if (isPostMethod) return postUser(body);
        else if (isGetMethod) return getUser(path);
      }

      if ("/login".equals(path)) {
        if (isPostMethod) return handlePostLogin(body);
      }

      if (path.startsWith("/answers")) {
        if (isPostMethod) return postAnswer(body);
        else if (isGetMethod) return getAnswers(path);
      }
    }

    response.setStatusCode(404);
    return response;
  }

  private APIGatewayProxyResponseEvent getAnswers(String path) throws JsonProcessingException {
    var response = getDefaultApiGatewayProxyResponseEvent();
    var splittedPaths = path.split("/");
    if (splittedPaths.length > 2) {
      var username = splittedPaths[2];
      if (StringUtils.isNotBlank(username)) {
        var answers = answerService.getAnswersByUser(username);
        response.setStatusCode(200);
        response.setBody(objectMapper.writeValueAsString(answers));
        return response;
      }
    }

    response.setStatusCode(404);
    return response;
  }

  private APIGatewayProxyResponseEvent postAnswer(Object body) throws JsonProcessingException {
    var response = getDefaultApiGatewayProxyResponseEvent();
    Answer answer = objectMapper.readValue(body.toString(), Answer.class);
    Answer createdAnswer;
    try {
      createdAnswer = answerService.save(answer);
      response.setStatusCode(201);
      response.setBody(objectMapper.writeValueAsString(createdAnswer));
    } catch (ValidationException e) {
      response.setBody(e.getMessage());
      response.setStatusCode(400);
    }
    return response;
  }

  private APIGatewayProxyResponseEvent handlePostLogin(Object body) throws JsonProcessingException {
    var response = getDefaultApiGatewayProxyResponseEvent();
    Map<String, String> bodyMap = objectMapper.readValue(body.toString(), LinkedHashMap.class);
    User foundUser = userService.login(bodyMap.get("username"), bodyMap.get("password"));
    if (foundUser != null) {
      response.setStatusCode(200);
      response.setBody(objectMapper.writeValueAsString(foundUser));
    } else {
      response.setStatusCode(400);
      response.setBody("username_password_not_correct");
    }
    return response;
  }

  private APIGatewayProxyResponseEvent getUser(String path) throws JsonProcessingException {
    var response = getDefaultApiGatewayProxyResponseEvent();
    var splittedPaths = path.split("/");
    if (splittedPaths.length > 2) {
      var username = splittedPaths[2];
      if (StringUtils.isNotBlank(username)) {
        User foundUser = userService.getUser(username);
        if (foundUser != null) {
          response.setBody(objectMapper.writeValueAsString(foundUser));
          response.setStatusCode(200);
        } else {
          response.setStatusCode(404);
        }
        return response;
      }
    }
    response.setStatusCode(404);
    return response;
  }

  private APIGatewayProxyResponseEvent postUser(Object body)
      throws com.fasterxml.jackson.core.JsonProcessingException {
    var response = getDefaultApiGatewayProxyResponseEvent();
    User user = objectMapper.readValue(body.toString(), User.class);
    User createdUser = null;
    try {
      createdUser = userService.save(user);
    } catch (ValidationException e) {
      response.setStatusCode(400);
      response.setBody(e.getMessage());
      return response;
    }
    response.setStatusCode(201);
    response.setBody(objectMapper.writeValueAsString(createdUser));
    return response;
  }

  private APIGatewayProxyResponseEvent getDefaultApiGatewayProxyResponseEvent() {
    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
    Map<String, String> responseHeaders = new HashMap<>();
    responseHeaders.put("Content-Type", "application/json");
    responseHeaders.put("Access-Control-Allow-Origin", "*");
    response.setHeaders(responseHeaders);
    response.setMultiValueHeaders(Collections.emptyMap());
    return response;
  }
}
