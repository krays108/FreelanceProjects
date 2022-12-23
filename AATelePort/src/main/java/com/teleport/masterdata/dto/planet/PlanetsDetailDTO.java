package com.teleport.masterdata.dto.planet;

import com.teleport.masterdata.model.Planet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanetsDetailDTO {
  private String name;
  private String iauCode;
  private String m49Name;
  private String m49Code;

  private Boolean isActive;

  public PlanetsDetailDTO(Planet planet) {
    this.name = planet.getName();
    this.iauCode = planet.getIauCode();
    this.m49Name = planet.getM49Name();
    this.m49Code = planet.getM49Code();
    this.isActive = planet.getIsActive();
  }
}
