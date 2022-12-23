package com.teleport.masterdata.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Calendar;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel {
  @Id
  @Setter(AccessLevel.PROTECTED)
  private String id;

  @Field("created_date_time")
  private Date createdDateTime = Calendar.getInstance().getTime();

  @Field("updated_date_time")
  private Date updatedDateTime;

  @Field("created_by")
  private String createdBy = "SYSTEM";

  @Field("updated_by")
  private String updatedBy;

  public BaseModel(String id) {
    this.id = id;
  }
}
