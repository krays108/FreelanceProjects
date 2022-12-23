package com.teleport.masterdata.model;

        import lombok.AllArgsConstructor;
        import lombok.Data;
        import lombok.NoArgsConstructor;
        import org.springframework.data.mongodb.core.mapping.Document;
        import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "VehicleType")
public class VehicleType extends BaseModel {

    @Field("vehicle_type_name")
    private String vehicleTypeName;

    @Field("vehicle_type_code")
    private String vehicleTypeCode;

    private Boolean isActive = true;
}
