package com.teleport.masterdata.dto.planet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseDTO {
  private List<PlanetsDetailDTO> planets;
}
