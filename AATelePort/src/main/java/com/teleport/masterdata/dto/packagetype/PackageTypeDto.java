package com.teleport.masterdata.dto.packagetype;

import com.teleport.masterdata.model.PackageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageTypeDto {

  private String packageTypeName;
  private String packageTypeCode;
  private Boolean isActive;

  public PackageTypeDto(PackageType packageType) {
    this.packageTypeName = packageType.getPackageTypeName();
    this.packageTypeCode = packageType.getPackageTypeCode();
    this.isActive = packageType.getIsActive();
  }
}
