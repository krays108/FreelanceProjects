package com.teleport.masterdata.service;

import com.teleport.masterdata.dto.intermediateregion.*;
import com.teleport.masterdata.model.*;
import com.teleport.masterdata.repository.IntermediateRegionRepository;
import com.teleport.masterdata.repository.PlanetRepository;
import com.teleport.masterdata.repository.RegionRepository;
import com.teleport.masterdata.repository.SubRegionRepository;
import com.teleport.masterdata.utils.IntermediateRegionIdentifier;
import com.teleport.masterdata.utils.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IntermediateRegionService {

  @Autowired IntermediateRegionRepository intermediateRegionRepository;
  @Autowired SubRegionRepository subRegionRepository;
  @Autowired RegionRepository regionRepository;
  @Autowired PlanetRepository planetRepository;

  public ResponseEntity<RestResponseIntermediateRegionsDTO> getIntermediateRegions(
      Status status, String language) {
    List<IntermediateRegion> intermediateRegionList;
    switch (status) {
      case active:
        intermediateRegionList =
            intermediateRegionRepository.findByIsActive(true).stream().collect(Collectors.toList());
        break;
      case disabled:
        intermediateRegionList =
            intermediateRegionRepository.findByIsActive(false).stream()
                .collect(Collectors.toList());
        break;
      default:
        intermediateRegionList =
            intermediateRegionRepository.findAllByOrderByIsActiveDesc().stream()
                .collect(Collectors.toList());
        break;
    }

    List<IntermediateRegionsDetailDTO> filteredList =
        intermediateRegionList.stream()
            .map(
                intermediateRegion ->
                    new IntermediateRegionsDetailDTO(
                        intermediateRegion,
                        subRegionRepository.findAllByOrderByIsActiveDesc().stream()
                            .filter(
                                subRegion ->
                                    subRegion.getId().equals(intermediateRegion.getSubRegionId()))
                            .collect(Collectors.toList())
                            .get(0)))
            .collect(Collectors.toList());

    if (filteredList.isEmpty()) {
      log.info("There are no intermediate-regions available in {} status", status);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    RestResponseIntermediateRegionsDTO intermediateRegions =
        new RestResponseIntermediateRegionsDTO();
    intermediateRegions.setIntermediateRegions(filteredList);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_LANGUAGE, language)
        .body(intermediateRegions);
  }

  public ResponseEntity<RestResponseIntermediateRegionsDTO> getIntermediateRegion(
      IntermediateRegionIdentifier identifier, String value, String language) {
    List<IntermediateRegion> intermediateRegionList;
    List<IntermediateRegion> allIntermediateRegions;
    List<String> subregionIds = new ArrayList<>();

    if (value.isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    switch (identifier) {
      case m49_planet:
        // To be refined after implementing @DocumentReference
        allIntermediateRegions = intermediateRegionRepository.findAllByOrderByIsActiveDesc();
        Planet planet = planetRepository.findByM49Code(value).get(0);
        List<Region> allRegions =
            regionRepository.findAllByOrderByIsActiveDesc().stream()
                .filter(regions -> regions.getPlanetId().equals(planet.getId()))
                .collect(Collectors.toList());
        List<String> regionIds = new ArrayList<>();
        allRegions.forEach(region -> regionIds.add(region.getId()));
        List<SubRegion> allSubRegions =
            subRegionRepository.findAllByOrderByIsActiveDesc().stream()
                .filter(subRegion -> regionIds.contains(subRegion.getRegionId()))
                .collect(Collectors.toList());
        allSubRegions.forEach(subRegion -> subregionIds.add(subRegion.getId()));
        intermediateRegionList =
            allIntermediateRegions.stream()
                .filter(
                    intermediateRegion ->
                        subregionIds.contains(intermediateRegion.getSubRegionId()))
                .collect(Collectors.toList());
        break;
      case m49_region:
        // To be refined after implementing @DocumentReference
        allIntermediateRegions = intermediateRegionRepository.findAllByOrderByIsActiveDesc();
        Region region = regionRepository.findByM49Code(value).get(0);
        List<SubRegion> allSubregions =
            subRegionRepository.findAllByOrderByIsActiveDesc().stream()
                .filter(subRegion -> subRegion.getRegionId().equals(region.getId()))
                .collect(Collectors.toList());
        allSubregions.forEach(subRegion -> subregionIds.add(subRegion.getId()));
        intermediateRegionList =
            allIntermediateRegions.stream()
                .filter(
                    intermediateRegion ->
                        subregionIds.contains(intermediateRegion.getSubRegionId()))
                .collect(Collectors.toList());
        break;
      case m49_sub:
        // To be refined after implementing @DocumentReference
        allIntermediateRegions = intermediateRegionRepository.findAllByOrderByIsActiveDesc();
        SubRegion subRegion = subRegionRepository.findByM49Code(value).get(0);
        intermediateRegionList =
            allIntermediateRegions.stream()
                .filter(
                    intermediateRegion ->
                        intermediateRegion.getSubRegionId().equals(subRegion.getId()))
                .collect(Collectors.toList());
        break;
      case m49:
        intermediateRegionList =
            intermediateRegionRepository.findByM49CodeOrderByIsActiveDesc(value);
        break;
      default:
        intermediateRegionList =
            intermediateRegionRepository.findAllByOrderByIsActiveDesc().stream()
                .filter(
                    intermediateRegion ->
                        StringUtils.containsIgnoreCase(
                            intermediateRegion.getIntermediateRegionName(), value))
                .collect(Collectors.toList());
        break;
    }

    // To be refined after implementing @DocumentReference
    List<IntermediateRegionsDetailDTO> filteredList =
        intermediateRegionList.stream()
            .map(
                intermediateRegion ->
                    new IntermediateRegionsDetailDTO(
                        intermediateRegion,
                        subRegionRepository.findAllByOrderByIsActiveDesc().stream()
                            .filter(
                                subRegion ->
                                    subRegion.getId().equals(intermediateRegion.getSubRegionId()))
                            .collect(Collectors.toList())
                            .get(0)))
            .collect(Collectors.toList());

    if (filteredList.isEmpty()) {
      log.info("There are no intermediate-regions found with {}: {}", identifier, value);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    RestResponseIntermediateRegionsDTO intermediateRegions =
        new RestResponseIntermediateRegionsDTO();
    intermediateRegions.setIntermediateRegions(filteredList);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_LANGUAGE, language)
        .body(intermediateRegions);
  }

  public ResponseEntity<String> updateIntermediateRegion(
      RestRequestIntermediateRegionDTO request) {
    if (request.getCode().isBlank()) {
      log.info("Invalid m49_code: ", request.getCode());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    List<IntermediateRegion> intermediateRegionList =
        intermediateRegionRepository.findByIntermediateRegionCode(
                request.getCode());
    if (intermediateRegionList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      IntermediateRegion intermediateRegion = intermediateRegionList.get(0);
      intermediateRegion.setIsActive(request.getIsActive());
      intermediateRegionRepository.save(intermediateRegion);
    }
    return ResponseEntity.of(Optional.of("Updated Successfully"));
  }
}
