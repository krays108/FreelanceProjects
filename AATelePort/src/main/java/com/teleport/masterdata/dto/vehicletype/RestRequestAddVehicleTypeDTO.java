package com.teleport.masterdata.dto.vehicletype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestAddVehicleTypeDTO {
    @NonNull private String vehicleType;
}
