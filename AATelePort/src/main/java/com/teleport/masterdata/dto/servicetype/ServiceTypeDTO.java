package com.teleport.masterdata.dto.servicetype;

import com.teleport.masterdata.model.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeDTO {

  @NonNull private String serviceTypeCode;

  private String serviceTypeName;

  private Boolean isActive;

  public ServiceTypeDTO(ServiceType serviceType) {
    this.serviceTypeCode = serviceType.getServiceTypeCode();
    this.serviceTypeName = serviceType.getServiceTypeName();
    this.isActive = serviceType.getIsActive();
  }
}
