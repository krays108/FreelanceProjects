package com.teleport.masterdata.dto.planet;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestDTO {
  @NonNull private String code;

  private Boolean isActive;
}
