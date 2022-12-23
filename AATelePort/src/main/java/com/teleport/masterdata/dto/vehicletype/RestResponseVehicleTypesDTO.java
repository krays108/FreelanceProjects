package com.teleport.masterdata.dto.vehicletype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseVehicleTypesDTO {
    private List<VehicleTypeDTO> vehicleTypes;
}
