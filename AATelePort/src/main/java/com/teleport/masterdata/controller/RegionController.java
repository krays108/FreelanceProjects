package com.teleport.masterdata.controller;

import com.teleport.masterdata.utils.RegionIdentifier;
import com.teleport.masterdata.utils.Status;
import com.teleport.masterdata.dto.region.RestRequestRegionDTO;
import com.teleport.masterdata.dto.region.RestResponseRegionsDTO;
import com.teleport.masterdata.service.RegionService;
import com.teleport.masterdata.model.Region;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@EnableAutoConfiguration
@RequestMapping("/api/v1")
@Slf4j
@Api(tags = {"Region APIs"})
public class RegionController {

  @Autowired RegionService regionService;

  @ApiOperation(value = "Get all Regions based on status", response = Region.class)
  @GetMapping("/regions")
  public ResponseEntity<RestResponseRegionsDTO> getRegions(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("status") Status status)
      throws Exception {
    log.info("Calling get Regions API with status: {}", status);
    return regionService.getRegions(status, language);
  }

  @ApiOperation(value = "Get all Regions based on a selected identifier", response = Region.class)
  @GetMapping("/region")
  public ResponseEntity<RestResponseRegionsDTO> getRegion(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("identifier-type") RegionIdentifier identifier,
      @RequestParam String value) {
    log.info("Calling get Regions API with {}: {}", identifier, value);
    return regionService.getRegion(identifier, value, language);
  }

  @ApiOperation(value = "Region activation and deactivation")
  @PutMapping(value = "/region")
  public ResponseEntity<String> updateRegion(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody RestRequestRegionDTO request) {
    log.info("Updating Region with m49_code: {}", request.getCode());
    return regionService.updateRegion(request, language);
  }
}
