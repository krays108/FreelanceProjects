package com.teleport.masterdata.controller;

import com.teleport.masterdata.dto.planet.RestRequestDTO;
import com.teleport.masterdata.dto.planet.RestResponseDTO;
import com.teleport.masterdata.service.PlanetService;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.utils.PlanetIdentifier;
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
@Api(tags = "Planet APIs")
public class PlanetController {

  @Autowired private PlanetService planetService;

  @ApiOperation(value = "Get all the Planets based on status ", response = Planet.class)
  @GetMapping(value = "/planets")
  public ResponseEntity<RestResponseDTO> getPlanets(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("status") Status status)
      throws Exception {
    log.info("Calling get planets API with status: {}", status);
    return planetService.getPlanets(status, language);
  }

  @ApiOperation(
      value = "Get all the Planets based on the selected identifier",
      response = Planet.class)
  @GetMapping(value = "/planet")
  public ResponseEntity<RestResponseDTO> getPlanet(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestParam("identifier-type") PlanetIdentifier identifier,
      @RequestParam String value) {
    log.info("Calling get planets API with {}: {}", identifier, value);
    return planetService.getPlanet(identifier, value, language);
  }

  @ApiOperation(value = "Planet activation and deactivation", response = Planet.class)
  @PutMapping(value = "/planet")
  public ResponseEntity<String> updatePlanet(
      @RequestHeader(value = "Accept-language", defaultValue = "en") String language,
      @RequestBody RestRequestDTO request) {
    log.info("Updating planet with iau_code: {}", request.getCode());
    return planetService.updatePlanet(request, language);
  }
}
