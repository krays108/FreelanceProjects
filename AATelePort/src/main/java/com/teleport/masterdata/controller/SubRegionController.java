package com.teleport.masterdata.controller;

import com.teleport.masterdata.dto.subregion.RestRequestSubRegionDTO;
import com.teleport.masterdata.dto.subregion.RestResponseSubRegionsDTO;
import com.teleport.masterdata.service.SubRegionService;
import com.teleport.masterdata.model.*;
import com.teleport.masterdata.utils.Status;
import com.teleport.masterdata.utils.SubRegionIdentifier;
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
@Api(tags = {"Sub Region APIs"})
public class SubRegionController {

  @Autowired SubRegionService subRegionService;

  @ApiOperation(value = "Get all the sub-regions in a given region", response = SubRegion.class)
  @GetMapping("/sub-regions")
  public ResponseEntity<RestResponseSubRegionsDTO> getSubRegions(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("status") Status status)
      throws Exception {
    log.info("Calling get Sub-regions API with status: {}", status);
    return subRegionService.getSubRegions(status, language);
  }

  @ApiOperation(
      value = "Get the sub-regions based on selected identifier",
      response = SubRegion.class)
  @GetMapping("/sub-region")
  public ResponseEntity<RestResponseSubRegionsDTO> getSubRegion(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("identifier-type") SubRegionIdentifier identifier,
      @RequestParam String value) {
    log.info("Calling get Sub-regions API with {}: {}", identifier, value);
    return subRegionService.getSubRegion(identifier, value, language);
  }

  @ApiOperation(value = "Sub-region activation and deactivation")
  @PutMapping(value = "/sub-region")
  public ResponseEntity<String> updateSubRegion(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody RestRequestSubRegionDTO request) {
    log.info("Updating Sub-region with m49_code: {}", request.getCode());
    return subRegionService.updateSubRegion(request, language);
  }
}
