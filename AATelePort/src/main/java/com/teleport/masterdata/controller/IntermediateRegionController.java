package com.teleport.masterdata.controller;

import com.teleport.masterdata.dto.intermediateregion.RestRequestIntermediateRegionDTO;
import com.teleport.masterdata.dto.intermediateregion.RestResponseIntermediateRegionsDTO;
import com.teleport.masterdata.service.IntermediateRegionService;
import com.teleport.masterdata.model.IntermediateRegion;
import com.teleport.masterdata.utils.IntermediateRegionIdentifier;
import com.teleport.masterdata.utils.Status;
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
@Api(tags = {"Intermediate Region APIs"})
public class IntermediateRegionController {

  @Autowired IntermediateRegionService intermediateRegionService;

  @ApiOperation(
      value = "Get all the intermediate-regions in a given sub-region",
      response = IntermediateRegion.class)
  @GetMapping("/intermediate-regions")
  public ResponseEntity<RestResponseIntermediateRegionsDTO> getIntermediateRegions(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("status") Status status)
      throws Exception {
    log.info("Calling get intermediate-regions API with status: {}", status);
    return intermediateRegionService.getIntermediateRegions(status, language);
  }

  @ApiOperation(
      value = "Get the intermediate-region details using intermediate-region code and name",
      response = IntermediateRegion.class)
  @GetMapping("/intermediate-region")
  public ResponseEntity<RestResponseIntermediateRegionsDTO> getIntermediateRegion(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("identifier-type") IntermediateRegionIdentifier identifier,
      @RequestParam String value) {
    log.info("Calling get intermediate-region API with {}: {}", identifier, value);
    return intermediateRegionService.getIntermediateRegion(identifier, value, language);
  }

  @ApiOperation(value = "Update status of a given intermediate-region")
  @PutMapping(value = "/intermediate-region")
  public ResponseEntity<String> updateIntermediateRegion(
      @RequestBody RestRequestIntermediateRegionDTO request) {
    return intermediateRegionService.updateIntermediateRegion(request);
  }
}
