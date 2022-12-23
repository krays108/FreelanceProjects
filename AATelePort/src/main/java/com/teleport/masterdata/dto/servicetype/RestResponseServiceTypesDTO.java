package com.teleport.masterdata.dto.servicetype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseServiceTypesDTO {
  private List<ServiceTypeDTO> serviceTypes;
}
