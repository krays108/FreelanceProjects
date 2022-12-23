package com.teleport.masterdata.service;

import com.teleport.masterdata.dto.planet.PlanetsDetailDTO;
import com.teleport.masterdata.dto.planet.RestRequestDTO;
import com.teleport.masterdata.dto.planet.RestResponseDTO;
import com.teleport.masterdata.exception.MasterDataException;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.repository.PlanetRepository;
import com.teleport.masterdata.utils.Constant;
import com.teleport.masterdata.utils.ErrorResponse;
import com.teleport.masterdata.utils.PlanetIdentifier;
import com.teleport.masterdata.utils.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlanetService {

  @Autowired PlanetRepository planetRepository;

  @Autowired ErrorResponse errorResponse;

  public ResponseEntity<RestResponseDTO> getPlanets(Status status, String language) {
    List<PlanetsDetailDTO> filteredPlanetList;
    RestResponseDTO planets = new RestResponseDTO();

    switch (status) {
      case active:
        filteredPlanetList =
            planetRepository.findByIsActive(true).stream()
                .map(PlanetsDetailDTO::new)
                .collect(Collectors.toList());
        break;
      case disabled:
        filteredPlanetList =
            planetRepository.findByIsActive(false).stream()
                .map(PlanetsDetailDTO::new)
                .collect(Collectors.toList());
        break;
      default:
        filteredPlanetList =
            planetRepository.findAllByOrderByIsActiveDesc().stream()
                .map(PlanetsDetailDTO::new)
                .collect(Collectors.toList());
        break;
    }
    planets.setPlanets(filteredPlanetList);

    if (filteredPlanetList.isEmpty()) {
      log.info("There are no planets available");
      errorResponse =
          new ErrorResponse(
              Constant.LM_0001,
              "Planet not Available ",
              "There are no planets available",
              "There are no planets available");
      throw new MasterDataException(errorResponse);
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(planets);
  }

  public ResponseEntity<RestResponseDTO> getPlanet(
      PlanetIdentifier identifier, String value, String language) {
    List<PlanetsDetailDTO> planetList;

    if (value.isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (identifier == PlanetIdentifier.m49) {
      planetList =
          planetRepository.findByM49CodeOrderByIsActiveDesc(value).stream()
              .map(PlanetsDetailDTO::new)
              .collect(Collectors.toList());
    } else {
      planetList =
          planetRepository.findAllByOrderByIsActiveDesc().stream()
              .filter(planet -> StringUtils.containsIgnoreCase(planet.getName(), value))
              .map(PlanetsDetailDTO::new)
              .collect(Collectors.toList());
    }

    if (planetList.isEmpty()) {
      log.info("There are no planets found with {}: {}", identifier, value);
      errorResponse =
          new ErrorResponse(
              Constant.LM_0002,
              "There are no planets ",
              "There are no planets found ",
              "There are no planets found given Identifier and Value");
      throw new MasterDataException(errorResponse);
    }

    RestResponseDTO planets = new RestResponseDTO();
    planets.setPlanets(planetList);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(planets);
  }

  public ResponseEntity<String> updatePlanet(RestRequestDTO request, String language) {
    if (StringUtils.isBlank(request.getCode()) || request.getIsActive() == null) {
      log.info("Invalid request: ", request);
      errorResponse =
          new ErrorResponse(
              Constant.LM_0003, "Invalid request ", "Invalid request", "Invalid request");
      throw new MasterDataException(errorResponse);
    }
    List<Planet> planetList = planetRepository.findByM49Code(request.getCode());
    if (planetList.isEmpty()) {
      log.info("There are no planets found with m49_code: {}", request.getCode());
      errorResponse =
          new ErrorResponse(
              Constant.LM_0004,
              "There are no planets found with m49_code ",
              "There are no planets found with m49_code",
              "There are no planets found with m49_code");
      throw new MasterDataException(errorResponse);
    } else {
      Planet planet = planetList.get(0);
      planet.setIsActive(request.getIsActive());
      Date date = Calendar.getInstance().getTime();
      planet.setUpdatedDateTime(date);
      planetRepository.save(planet);
      log.info("Planet successfully updated");
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }
}
