package org.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web.common.BaseEntity;
import org.web.common.DateCommonUtil;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends BaseEntity {
  private String id;
  private String username;
  private Integer questionNo;
  private Integer point;

  public static Answer from(Map<String, AttributeValue> item) {
    if (item != null && !item.isEmpty()) {
      Answer answer = new Answer();
      answer.setId(item.get("id").s());
      answer.setUsername(item.get("username").s());
      answer.setPoint(Integer.parseInt(item.get("point").n()));
      answer.setQuestionNo(Integer.parseInt(item.get("questionNo").n()));
      answer.setCreatedAt(DateCommonUtil.stringToDate(item.get("createdAt").s()));
      answer.setUpdatedAt(DateCommonUtil.stringToDate(item.get("updatedAt").s()));

      return answer;
    }
    return null;
  }

}
