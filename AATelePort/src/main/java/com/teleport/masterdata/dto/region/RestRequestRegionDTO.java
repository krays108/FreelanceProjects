package com.teleport.masterdata.dto.region;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestRegionDTO {
  @NonNull private String code;

  private Boolean isActive;
}
