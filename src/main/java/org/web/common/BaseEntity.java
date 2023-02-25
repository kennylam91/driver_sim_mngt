package org.web.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public abstract class BaseEntity {
  private String id;
  private Date createdAt;
  private Date updatedAt;
}
