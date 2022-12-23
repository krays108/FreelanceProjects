package com.teleport.masterdata.dto.servicetype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestAddServiceTypeDTO {
  @NonNull private String serviceTypeName;
}
