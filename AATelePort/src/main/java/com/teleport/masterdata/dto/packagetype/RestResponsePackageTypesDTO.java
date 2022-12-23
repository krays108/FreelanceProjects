package com.teleport.masterdata.dto.packagetype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponsePackageTypesDTO {

  public List<PackageTypeDto> packageTypes;
}
