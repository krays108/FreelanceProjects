package com.teleport.masterdata.dto.intermediateregion;

import com.teleport.masterdata.model.IntermediateRegion;
import com.teleport.masterdata.model.SubRegion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntermediateRegionsDetailDTO {
  private String name;
  private String intermediateRegionCode;
  private String m49Code;
  private Boolean isActive;
  private String subRegion;
  private String subRegionM49Code;

  public IntermediateRegionsDetailDTO(IntermediateRegion ir) {
    this.name = ir.getIntermediateRegionName();
    this.intermediateRegionCode = ir.getIntermediateRegionCode();
    this.m49Code = ir.getM49Code();
    this.isActive = ir.getIsActive();
    if (ir.getSubRegion() != null) {
      this.subRegion = ir.getSubRegion().getSubRegionName();
      this.subRegionM49Code = ir.getSubRegion().getM49Code();
    }
  }

  public IntermediateRegionsDetailDTO(IntermediateRegion ir, SubRegion sr) {
    this.name = ir.getIntermediateRegionName();
    this.intermediateRegionCode = ir.getIntermediateRegionCode();
    this.m49Code = ir.getM49Code();
    this.isActive = ir.getIsActive();
    this.subRegion = sr.getSubRegionName();
    this.subRegionM49Code = sr.getM49Code();
  }
}
