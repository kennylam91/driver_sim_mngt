package org.web.user.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web.common.BaseEntity;
import org.web.common.DateCommonUtil;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

@Data
@NoArgsConstructor
@RegisterForReflection
public class User extends BaseEntity {
  private String username;
  private String password;
  private String email;
  private String phone;

  public static User from(Map<String, AttributeValue> item) {
    User user = new User();
    if (item != null && !item.isEmpty()) {
      user.setId(item.get("id").s());
      user.setUsername(item.get("username").s());
      user.setEmail(item.get("email").s());
      user.setPhone(item.get("phone").s());
      user.setCreatedAt(DateCommonUtil.stringToDate(item.get("createdAt").s()));
      user.setUpdatedAt(DateCommonUtil.stringToDate(item.get("updatedAt").s()));
    }
    return user;
  }
}
