package com.teleport.masterdata.service;

import com.teleport.masterdata.dto.subregion.*;
import com.teleport.masterdata.model.*;
import com.teleport.masterdata.repository.PlanetRepository;
import com.teleport.masterdata.repository.RegionRepository;
import com.teleport.masterdata.repository.SubRegionRepository;
import com.teleport.masterdata.utils.Status;
import com.teleport.masterdata.utils.SubRegionIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubRegionService {

  @Autowired SubRegionRepository subRegionRepository;
  @Autowired RegionRepository regionRepository;
  @Autowired PlanetRepository planetRepository;

  public ResponseEntity<RestResponseSubRegionsDTO> getSubRegions(Status status, String language) {
    List<SubRegion> subRegionList;
    switch (status) {
      case active:
        subRegionList =
            subRegionRepository.findByIsActive(true).stream().collect(Collectors.toList());
        break;
      case disabled:
        subRegionList =
            subRegionRepository.findByIsActive(false).stream().collect(Collectors.toList());
        break;
      default:
        subRegionList =
            subRegionRepository.findAllByOrderByIsActiveDesc().stream()
                .collect(Collectors.toList());
        break;
    }

    List<SubRegionsDetailDTO> filteredSubRegionList =
        subRegionList.stream()
            .map(
                subRegion ->
                    new SubRegionsDetailDTO(
                        subRegion,
                        regionRepository.findAllByOrderByIsActiveDesc().stream()
                            .filter(region -> region.getId().equals(subRegion.getRegionId()))
                            .collect(Collectors.toList())
                            .get(0)))
            .collect(Collectors.toList());

    if (filteredSubRegionList.isEmpty()) {
      log.info("There are no sub-regions available in {} status", status);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    RestResponseSubRegionsDTO subRegions = new RestResponseSubRegionsDTO();
    subRegions.setSubRegions(filteredSubRegionList);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(subRegions);
  }

  public ResponseEntity<RestResponseSubRegionsDTO> getSubRegion(
      SubRegionIdentifier identifier, String value, String language) {
    List<SubRegion> subRegionList;
    List<SubRegion> allSubRegions;

    if (value.isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    switch (identifier) {
      case m49_region:
        // To be refined after implementing @DocumentReference
        allSubRegions = subRegionRepository.findAllByOrderByIsActiveDesc();
        Region region = regionRepository.findByM49Code(value).get(0);
        subRegionList =
            allSubRegions.stream()
                .filter(subRegion -> subRegion.getRegionId().equals(region.getId()))
                .collect(Collectors.toList());
        break;
      case m49_planet:
        // To be refined after implementing @DocumentReference
        allSubRegions = subRegionRepository.findAllByOrderByIsActiveDesc();
        Planet planet = planetRepository.findByM49Code(value).get(0);
        List<Region> allRegions =
            regionRepository.findAllByOrderByIsActiveDesc().stream()
                .filter(regions -> regions.getPlanetId().equals(planet.getId()))
                .collect(Collectors.toList());
        List<String> regionIds = new ArrayList<>();
        allRegions.forEach(region1 -> regionIds.add(region1.getId()));
        subRegionList =
            allSubRegions.stream()
                .filter(subRegion -> regionIds.contains(subRegion.getRegionId()))
                .collect(Collectors.toList());
        break;
      case name:
        subRegionList =
            subRegionRepository.findAllByOrderByIsActiveDesc().stream()
                .filter(
                    subRegion -> StringUtils.containsIgnoreCase(subRegion.getSubRegionName(), value))
                .collect(Collectors.toList());
        break;
      default:
        subRegionList = subRegionRepository.findByM49CodeOrderByIsActiveDesc(value);
        break;
    }

    // To be refined after implementing @DocumentReference
    List<SubRegionsDetailDTO> filteredSubRegionList =
        subRegionList.stream()
            .map(
                subRegion ->
                    new SubRegionsDetailDTO(
                        subRegion,
                        regionRepository.findAllByOrderByIsActiveDesc().stream()
                            .filter(region -> region.getId().equals(subRegion.getRegionId()))
                            .collect(Collectors.toList())
                            .get(0)))
            .collect(Collectors.toList());

    if (filteredSubRegionList.isEmpty()) {
      log.info("There are no sub-regions found with {}: {}", identifier, value);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    RestResponseSubRegionsDTO subRegions = new RestResponseSubRegionsDTO();
    subRegions.setSubRegions(filteredSubRegionList);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(subRegions);
  }

  public ResponseEntity<String> updateSubRegion(
      RestRequestSubRegionDTO request, String language) {
    if (StringUtils.isBlank(request.getCode()) || request.getIsActive() == null) {
      log.info("Invalid m49_code: ", request.getCode());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    List<SubRegion> subRegionList = subRegionRepository.findByM49Code(request.getCode());
    if (subRegionList.isEmpty()) {
      log.info("There are no sub-regions found with m49_code: {}", request.getCode());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      SubRegion subRegion = subRegionList.get(0);
      subRegion.setIsActive(request.getIsActive());
      Date date = Calendar.getInstance().getTime();
      subRegion.setUpdatedDateTime(date);
      subRegionRepository.save(subRegion);
      log.info("Sub-region successfully updated");
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }
}
