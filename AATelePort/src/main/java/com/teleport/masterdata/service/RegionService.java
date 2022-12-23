package com.teleport.masterdata.service;

import com.teleport.masterdata.dto.region.*;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.model.Region;
import com.teleport.masterdata.repository.PlanetRepository;
import com.teleport.masterdata.repository.RegionRepository;
import com.teleport.masterdata.utils.Constant;
import com.teleport.masterdata.utils.RegionIdentifier;
import com.teleport.masterdata.utils.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegionService {

  @Autowired RegionRepository regionRepository;

  @Autowired PlanetRepository planetRepository;

  public ResponseEntity<RestResponseRegionsDTO> getRegions(Status status, String language) {
    List<RegionsDetailDTO> filteredRegionList;
    RestResponseRegionsDTO regions = new RestResponseRegionsDTO();
    switch (status) {
      case active:
        filteredRegionList =
            regionRepository.findByIsActive(true).stream()
                .map(RegionsDetailDTO::new)
                .collect(Collectors.toList());
        break;
      case disabled:
        filteredRegionList =
            regionRepository.findByIsActive(false).stream()
                .map(RegionsDetailDTO::new)
                .collect(Collectors.toList());
        break;
      default:
        filteredRegionList =
            regionRepository.findAllByOrderByIsActiveDesc().stream()
                .map(RegionsDetailDTO::new)
                .collect(Collectors.toList());
        break;
    }
    regions.setRegions(filteredRegionList);

    if (filteredRegionList.isEmpty()) {
      log.info("There are no regions available");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(regions);
  }

  public ResponseEntity<RestResponseRegionsDTO> getRegion(
      RegionIdentifier identifier, String value, String language) {
    List<Region> regionList;
    List<Region> allRegions;

    if (value.isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    switch (identifier) {
      case m49:
        regionList = regionRepository.findByM49CodeOrderByIsActiveDesc(value);
        break;
      case name:
        allRegions = regionRepository.findAllByOrderByIsActiveDesc();
        regionList =
            allRegions.stream()
                .filter(region -> StringUtils.containsIgnoreCase(region.getRegionName(), value))
                .collect(Collectors.toList());
        break;
      default:
        allRegions = regionRepository.findAllByOrderByIsActiveDesc();
        regionList =
            allRegions.stream()
                .filter(
                    region ->
                        region.getPlanet() != null
                            && Objects.equals(region.getPlanet().getM49Code(), value))
                .collect(Collectors.toList());
        break;
    }

    List<RegionsDetailDTO> filteredRegionList =
        regionList.stream().map(RegionsDetailDTO::new).collect(Collectors.toList());

    RestResponseRegionsDTO regions = new RestResponseRegionsDTO();
    regions.setRegions(filteredRegionList);

    if (filteredRegionList.isEmpty()) {
      log.info("There are no regions found with {}: {}", identifier, value);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(regions);
  }

  public ResponseEntity<String> updateRegion(RestRequestRegionDTO request, String language) {
    if (StringUtils.isBlank(request.getCode()) || request.getIsActive() == null) {
      log.info("Invalid m49_code: ", request.getCode());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    List<Region> regionList = regionRepository.findByM49Code(request.getCode());
    if (regionList.isEmpty()) {
      log.info("There are no regions found with m49_code: {}", request.getCode());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      Region region = regionList.get(0);
      region.setIsActive(request.getIsActive());
      Date date = Calendar.getInstance().getTime();
      region.setUpdatedDateTime(date);
      regionRepository.save(region);
      log.info("Region successfully updated");
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }
}
