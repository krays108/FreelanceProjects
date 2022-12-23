package com.teleport.masterdata.dto.vehicletype;

import com.teleport.masterdata.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeDTO {

    private String vehicleType;

    @NonNull private String vehicleTypeCode;

    private Boolean isActive;

    public VehicleTypeDTO(VehicleType vehicleType) {
        this.vehicleType = vehicleType.getVehicleTypeName();
        this.vehicleTypeCode = vehicleType.getVehicleTypeCode();
        this.isActive = vehicleType.getIsActive();
    }
}
