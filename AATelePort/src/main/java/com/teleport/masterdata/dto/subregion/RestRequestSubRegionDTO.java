package com.teleport.masterdata.dto.subregion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestSubRegionDTO {
  @NonNull private String code;

  private Boolean isActive;
}
