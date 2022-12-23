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
@Document(collection = "Region")
public class Region extends BaseModel {

  @Field("region_name")
  private String regionName;

  @Field("region_code")
  private String regionCode;

  @Field("m49_code")
  private String m49Code;

  @Field("_planet_id")
  private String planetId;

  @ReadOnlyProperty
  @DocumentReference(lookup = "{ '_id.valueOf()' : ?#{#planetId} }")
  private Planet planet;

  private Boolean isActive;

  @Builder
  public Region(Region region, String id) {
    super(id);
    this.regionName = region.getRegionName();
    this.regionCode = region.getRegionCode();
    this.m49Code = region.getM49Code();
    this.planetId = region.getPlanetId();
    this.isActive = region.getIsActive();
  }
}
