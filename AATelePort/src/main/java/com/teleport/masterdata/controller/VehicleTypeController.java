package com.teleport.masterdata.controller;

import com.teleport.masterdata.dto.vehicletype.*;
import com.teleport.masterdata.model.VehicleType;
import com.teleport.masterdata.service.VehicleTypeService;
import com.teleport.masterdata.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@Api(tags = {"Vehicle Type APIs"})
public class VehicleTypeController {

  @Autowired VehicleTypeService vehicleTypeService;

  @ApiOperation(value = "Get all the vehicle-types", response = VehicleType.class)
  @GetMapping("/vehicle-types")
  public ResponseEntity<RestResponseVehicleTypesDTO> getVehicleTypes(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("status") Status status)
      throws Exception {
    log.info("Calling get Vehicle-types API with status: {}", status);
    return vehicleTypeService.getVehicleTypes(status, language);
  }

  @ApiOperation(
      value = "Get the vehicle-types based on selected identifier",
      response = VehicleType.class)
  @GetMapping("/vehicle-type")
  public ResponseEntity<RestResponseVehicleTypesDTO> getVehicleType(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("identifier-type") VehicleTypeIdentifier identifier,
      @RequestParam String value) {
    log.info("Calling get Vehicle-type API with {}: {}", identifier, value);
    return vehicleTypeService.getVehicleType(identifier, value, language);
  }

  @ApiOperation(value = "Create a vehicle type", response = VehicleType.class)
  @PostMapping("/vehicle-type")
  public ResponseEntity<String> addVehicleType(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody RestRequestAddVehicleTypeDTO request)
      throws Exception {
    log.info("Creating Vehicle-type with name {}", request.getVehicleType());
    return vehicleTypeService.addVehicleType(request.getVehicleType(), language);
  }

  @ApiOperation(value = "Update a vehicle type", response = VehicleType.class)
  @PutMapping("/vehicle-type")
  public ResponseEntity<String> updateVehicleType(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody VehicleTypeDTO request)
      throws Exception {
    log.info("Updating Vehicle-type with name {}", request.getVehicleType());
    return vehicleTypeService.updateVehicleType(request, language);
  }
}
