package com.teleport.masterdata.dto.region;

import com.teleport.masterdata.model.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionsDetailDTO {
  private String name;
  private String regionCode;
  private String m49Code;
  private Boolean isActive;
  private String planetName;
  private String planetM49Code;

  public RegionsDetailDTO(Region r) {
    this.name = r.getRegionName();
    this.regionCode = r.getRegionCode();
    this.m49Code = r.getM49Code();
    this.isActive = r.getIsActive();
    if(r.getPlanet() != null) {
      this.planetName = r.getPlanet().getName();
      this.planetM49Code = r.getPlanet().getM49Code();
    }
  }
}
