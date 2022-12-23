package com.teleport.masterdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "PackageType")
public class PackageType extends BaseModel {

  @Field("package_type_name")
  private String packageTypeName;

  @Field("package_type_code")
  private String packageTypeCode;

  private Boolean isActive;
}
