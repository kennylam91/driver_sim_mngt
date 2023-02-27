package org.web.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web.common.BaseEntity;
import org.web.common.DateCommonUtil;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class User extends BaseEntity {
  String username;
  private String password;
  private String email;
  private String phone;

  public static User from(Map<String, AttributeValue> item) {
    if (item != null && !item.isEmpty()) {
      User user = new User();
      if (item.get("id") != null) user.setId(item.get("id").s());
      if (item.get("username") != null) user.setUsername(item.get("username").s());
      if (item.get("email") != null) {
        user.setEmail(item.get("email").s());
      }
      if (item.get("phone") != null) {
        user.setPhone(item.get("phone").s());
      }
      if (item.get("createdAt") != null) {
        user.setCreatedAt(DateCommonUtil.stringToDate(item.get("createdAt").s()));
      }
      if (item.get("updatedAt") != null) {
        user.setUpdatedAt(DateCommonUtil.stringToDate(item.get("updatedAt").s()));
      }

      return user;
    }
    return null;
  }
}
