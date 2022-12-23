package com.teleport.masterdata.repository;

import com.teleport.masterdata.model.VehicleType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface VehicleTypeRepository extends MongoRepository<VehicleType, String> {

    @Query
    public List<VehicleType> findByVehicleTypeCode(String vehicleTypeCode);

    @Query
    public List<VehicleType> findByVehicleTypeCodeOrderByIsActiveDesc(String vehicleTypeCode);

    @Query
    public List<VehicleType> findByIsActive(boolean isActive);

    @Query
    public List<VehicleType> findAllByOrderByIsActiveDesc();
}
