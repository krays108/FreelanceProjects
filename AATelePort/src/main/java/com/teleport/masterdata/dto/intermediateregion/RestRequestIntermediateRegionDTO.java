package com.teleport.masterdata.dto.intermediateregion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestIntermediateRegionDTO {
  @NonNull private String code;
  private Boolean isActive;
}
