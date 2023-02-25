package org.web;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.web.user.entity.User;
import org.web.user.service.UserService;
import software.amazon.awssdk.utils.StringUtils;

import javax.inject.Inject;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, Object>, String> {

  @Inject
  UserService userService;
  @Inject
  ObjectMapper objectMapper;

  @SneakyThrows
  @Override
  public String handleRequest(Map<String, Object> event, Context context) {
    String path = (String) event.get("path");
    String httpMethod = (String) event.get("httpMethod");
    Object body = event.get("body");

    switch (path) {
      case "/users":
        switch (httpMethod) {
          case "POST":
            User user = objectMapper.readValue(body.toString(), User.class);
            var response = userService.save(user);
            return objectMapper.writeValueAsString(response);

          case "GET":
            Map<String, String> pathParameters = (Map<String, String>) event.get("pathParameters");
            var username = pathParameters.get("id");
            if (StringUtils.isNotBlank(username)) {
              User foundUser = userService.getUser(username);
              return objectMapper.writeValueAsString(foundUser);
            }

          default:
            return "not found";
        }
    }
    return "not found";
  }
}
