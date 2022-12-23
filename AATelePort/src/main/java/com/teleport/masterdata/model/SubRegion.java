package com.teleport.masterdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "SubRegion")
public class SubRegion extends BaseModel {

  @Field("sub_region")
  private String subRegionName;

  @Field("sub_region_code")
  private String subRegionCode;

  @Field("m49_code")
  private String m49Code;

  @Field("_region_id")
  private String regionId;

  @ReadOnlyProperty
  @DocumentReference(lookup = "{ '_id.valueOf()' : ?#{#regionId} }")
  private Region region;

  private Boolean isActive;

  @Builder
  public SubRegion(SubRegion subRegion, String id) {
    super(id);
    this.subRegionName = subRegion.getSubRegionName();
    this.subRegionCode = subRegion.getSubRegionCode();
    this.m49Code = subRegion.getM49Code();
    this.regionId = subRegion.getRegionId();
    this.isActive = subRegion.getIsActive();
  }
}
