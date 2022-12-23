package com.teleport.masterdata.controller;

import com.teleport.masterdata.dto.packagetype.PackageTypeDto;
import com.teleport.masterdata.dto.packagetype.RestRequestAddPackageTypeDTO;
import com.teleport.masterdata.dto.packagetype.RestResponsePackageTypesDTO;
import com.teleport.masterdata.model.PackageType;
import com.teleport.masterdata.service.PackageTypeService;
import com.teleport.masterdata.utils.PackageTypeIdentifier;
import com.teleport.masterdata.utils.Status;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@Api(tags = "Package Type  APIs")
public class PackageTypeController {

  @Autowired PackageTypeService packageTypeService;

  @ApiOperation(value = "Get all the PackageType based on status ", response = PackageType.class)
  @GetMapping(value = "/package-types")
  public ResponseEntity<RestResponsePackageTypesDTO> getPackageTypes(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("status") Status status)
      throws Exception {
    log.info("Calling get PackageType API with status: {}", status);
    return packageTypeService.getPackageTypes(status, language);
  }

  @ApiOperation(
      value = "Get all the PackageType based on the selected identifier",
      response = PackageType.class)
  @GetMapping(value = "/package-type")
  public ResponseEntity<RestResponsePackageTypesDTO> getPackageTypesByIdentifier(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("identifier-type") PackageTypeIdentifier identifier,
      @RequestParam String value) {
    log.info("Calling get PackageType API with {}: {}", identifier, value);
    return packageTypeService.getPackageTypes(identifier, value, language);
  }

  @ApiOperation(value = "Creating Package Type Collection", response = PackageType.class)
  @PostMapping(value = "/package-type")
  public ResponseEntity<String> createPackageType(
      @RequestBody RestRequestAddPackageTypeDTO request,
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language) {
    log.info("Creating Package Type in the DB");
    return packageTypeService.addPackageType(request.getPackageTypeName(), language);
  }

  @ApiOperation(value = "Updating Package Type Collection", response = PackageType.class)
  @PutMapping(value = "/package")
  public ResponseEntity<String> updatePackageType(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody PackageTypeDto request) {
    log.info("Updating  Package Type Collection ");
    packageTypeService.updatePackageType(request, language);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }
}
