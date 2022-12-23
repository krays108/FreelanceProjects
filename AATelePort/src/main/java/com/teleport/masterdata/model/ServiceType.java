package com.teleport.masterdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ServiceType")
public class ServiceType extends BaseModel {

  @Field("service_type_name")
  private String serviceTypeName;

  @Field("service_type_code")
  private String serviceTypeCode;

  private Boolean isActive = true;
}
