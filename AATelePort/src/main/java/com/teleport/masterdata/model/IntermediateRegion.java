package com.teleport.masterdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "IntermediateRegion")
public class IntermediateRegion extends BaseModel {

  @Field("intermediate_region")
  private String intermediateRegionName;

  @Field("intermediate_region_code")
  private String intermediateRegionCode;

  @Field("m49_code")
  private String m49Code;

  @Field("_sub_region_id")
  private String subRegionId;

  @ReadOnlyProperty
  @DocumentReference(lookup = "{ '_id.valueOf()' : ?#{#subRegionId} }")
  private SubRegion subRegion;

  private Boolean isActive;
}
