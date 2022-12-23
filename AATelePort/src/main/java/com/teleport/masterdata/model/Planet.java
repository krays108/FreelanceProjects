package com.teleport.masterdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Planet")
public class Planet extends BaseModel {
  @Field("planet_name")
  private String name;

  @Field("iau_code")
  private String iauCode;

  @Field("m49_code")
  private String m49Code;

  @Field("m49_name")
  private String m49Name;

  private Boolean isActive;

  @Builder
  public Planet(Planet planet, String id) {
    super(id);
    this.name = planet.getName();
    this.iauCode = planet.getIauCode();
    this.m49Code = planet.getM49Code();
    this.m49Name = planet.getM49Name();
    this.isActive = planet.getIsActive();
  }
}
