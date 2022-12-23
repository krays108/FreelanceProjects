package com.teleport.masterdata.dto.subregion;

import com.teleport.masterdata.model.Region;
import com.teleport.masterdata.model.SubRegion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubRegionsDetailDTO {
  private String name;
  private String subRegionCode;
  private String m49Code;
  private Boolean isActive;
  private String region;
  private String regionM49Code;

  public SubRegionsDetailDTO(SubRegion sr) {
    this.name = sr.getSubRegionName();
    this.subRegionCode = sr.getSubRegionCode();
    this.m49Code = sr.getM49Code();
    this.isActive = sr.getIsActive();
    if (sr.getRegion() != null) {
      this.region = sr.getRegion().getRegionName();
      this.regionM49Code = sr.getRegion().getM49Code();
    }
  }

  public SubRegionsDetailDTO(SubRegion sr, Region r) {
    this.name = sr.getSubRegionName();
    this.subRegionCode = sr.getSubRegionCode();
    this.m49Code = sr.getM49Code();
    this.isActive = sr.getIsActive();
    this.region = r.getRegionName();
    this.regionM49Code = r.getM49Code();
  }
}
