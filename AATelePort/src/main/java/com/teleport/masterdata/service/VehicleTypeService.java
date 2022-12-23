package com.teleport.masterdata.service;

import com.teleport.masterdata.dto.vehicletype.*;
import com.teleport.masterdata.model.VehicleType;
import com.teleport.masterdata.repository.VehicleTypeRepository;
import com.teleport.masterdata.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VehicleTypeService {

  @Autowired VehicleTypeRepository vehicleTypeRepository;

    public ResponseEntity<RestResponseVehicleTypesDTO> getVehicleTypes(
            Status status, String language) {
        List<VehicleTypeDTO> vehicleTypeList;

        switch (status) {
            case active:
                vehicleTypeList =
                        vehicleTypeRepository.findByIsActive(true).stream()
                                .map(VehicleTypeDTO::new)
                                .collect(Collectors.toList());
                break;
            case disabled:
                vehicleTypeList =
                        vehicleTypeRepository.findByIsActive(false).stream()
                                .map(VehicleTypeDTO::new)
                                .collect(Collectors.toList());
                break;
            default:
                vehicleTypeList =
                        vehicleTypeRepository.findAllByOrderByIsActiveDesc().stream()
                                .map(VehicleTypeDTO::new)
                                .collect(Collectors.toList());
                break;
        }

        if (vehicleTypeList.isEmpty()) {
            log.info("There are no vehicle-types available in {} status", status);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        RestResponseVehicleTypesDTO vehicleTypes = new RestResponseVehicleTypesDTO();
        vehicleTypes.setVehicleTypes(vehicleTypeList);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(vehicleTypes);
    }

    public ResponseEntity<RestResponseVehicleTypesDTO> getVehicleType(
            VehicleTypeIdentifier identifier, String value, String language) {

        if (value.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<VehicleTypeDTO> vehicleTypeList;
        if (identifier == VehicleTypeIdentifier.name) {
            vehicleTypeList =
                    vehicleTypeRepository.findAllByOrderByIsActiveDesc().stream()
                            .filter(
                                    vehicleType ->
                                            StringUtils.containsIgnoreCase(vehicleType.getVehicleTypeName(), value))
                            .map(VehicleTypeDTO::new)
                            .collect(Collectors.toList());
        } else {
            vehicleTypeList =
                    vehicleTypeRepository
                            .findByVehicleTypeCodeOrderByIsActiveDesc(value.toLowerCase())
                            .stream()
                            .map(VehicleTypeDTO::new)
                            .collect(Collectors.toList());
        }

        if (vehicleTypeList.isEmpty()) {
            log.info("There are no vehicle-types found with {}: {}", identifier, value);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        RestResponseVehicleTypesDTO vehicleTypes = new RestResponseVehicleTypesDTO();
        vehicleTypes.setVehicleTypes(vehicleTypeList);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(vehicleTypes);
    }

  public ResponseEntity<String> addVehicleType(String vehicleTypeName, String language) {
    if (vehicleTypeName.isBlank()) {
      log.info("Invalid request");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    String vehicleTypeCode = vehicleTypeName.toLowerCase().trim();
    vehicleTypeCode = vehicleTypeCode.replace(" ", "-");
    List<VehicleType> vehicleTypes = vehicleTypeRepository.findByVehicleTypeCode(vehicleTypeCode);
    if (!vehicleTypes.isEmpty()) {
      log.info("Vehicle type already exists");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } else {
      VehicleType newVehicleType = new VehicleType();
      newVehicleType.setVehicleTypeName(vehicleTypeName);
      newVehicleType.setVehicleTypeCode(vehicleTypeCode);
      vehicleTypeRepository.save(newVehicleType);
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }

  public ResponseEntity<String> updateVehicleType(VehicleTypeDTO request, String language) {
    if (StringUtils.isBlank(request.getVehicleTypeCode())
        || (StringUtils.isEmpty(request.getVehicleType()) && request.getIsActive() == null)) {
      log.info("Invalid request");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<VehicleType> vehicleTypes =
        vehicleTypeRepository.findByVehicleTypeCode(request.getVehicleTypeCode());
    if (vehicleTypes.isEmpty()) {
      log.info(
          "Vehicle type with vehicle-type-code {} doesn't exist", request.getVehicleTypeCode());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else if (vehicleTypes.size() > 1) {
      log.info("Multiple entry exists for vehicle-type-code {}", request.getVehicleTypeCode());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } else {
      VehicleType vehicleType = vehicleTypes.get(0);
      if (!StringUtils.isEmpty(request.getVehicleType())) {
        vehicleType.setVehicleTypeName(request.getVehicleType());
      }
      if (request.getIsActive() != null) {
        vehicleType.setIsActive(request.getIsActive());
      }
      Date updatedDate = Calendar.getInstance().getTime();
      vehicleType.setUpdatedDateTime(updatedDate);
      vehicleTypeRepository.save(vehicleType);
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }
}
