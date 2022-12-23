package com.teleport.masterdata.dto.region;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseRegionsDTO {
  private List<RegionsDetailDTO> regions;
}
