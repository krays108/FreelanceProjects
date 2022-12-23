package com.teleport.masterdata.controller;

import com.teleport.masterdata.dto.servicetype.*;
import com.teleport.masterdata.model.ServiceType;
import com.teleport.masterdata.service.ServiceTypeService;
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
@Api(tags = {"Service Type APIs"})
public class ServiceTypeController {

  @Autowired ServiceTypeService serviceTypeService;

  @ApiOperation(value = "Create a service type", response = ServiceType.class)
  @PostMapping("/service-type")
  public ResponseEntity<String> addServiceType(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody RestRequestAddServiceTypeDTO request)
      throws Exception {
    log.info("Creating Service-type with name {}", request.getServiceTypeName());
    return serviceTypeService.addServiceType(request.getServiceTypeName(), language);
  }

  @ApiOperation(value = "Update a service type", response = ServiceType.class)
  @PutMapping("/service-type")
  public ResponseEntity<String> updateServiceType(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody ServiceTypeDTO request)
      throws Exception {
    log.info("Updating Service-type with name {}", request.getServiceTypeName());
    return serviceTypeService.updateServiceType(request, language);
  }

  @ApiOperation(
      value = "Get the service-types based on selected identifier",
      response = ServiceType.class)
  @GetMapping("/service-type")
  public ResponseEntity<RestResponseServiceTypesDTO> getServiceType(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("identifier-type") ServiceTypeIdentifier identifier,
      @RequestParam String value) {
    log.info("Calling get Service-type API with {}: {}", identifier, value);
    return serviceTypeService.getServiceType(identifier, value, language);
  }

  @ApiOperation(value = "Get all the service-types", response = ServiceType.class)
  @GetMapping("/service-types")
  public ResponseEntity<RestResponseServiceTypesDTO> getServiceTypes(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("status") Status status)
      throws Exception {
    log.info("Calling get Service-types API with status: {}", status);
    return serviceTypeService.getServiceTypes(status, language);
  }
}
