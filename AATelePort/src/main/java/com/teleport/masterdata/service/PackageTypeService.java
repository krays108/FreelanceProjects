package com.teleport.masterdata.service;

import com.teleport.masterdata.dto.packagetype.PackageTypeDto;
import com.teleport.masterdata.dto.packagetype.RestResponsePackageTypesDTO;
import com.teleport.masterdata.exception.MasterDataException;
import com.teleport.masterdata.model.PackageType;
import com.teleport.masterdata.repository.PackageTypeRepository;
import com.teleport.masterdata.utils.Constant;
import com.teleport.masterdata.utils.ErrorResponse;
import com.teleport.masterdata.utils.PackageTypeIdentifier;
import com.teleport.masterdata.utils.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PackageTypeService {

  @Autowired PackageTypeRepository packageTypeRepository;

  @Autowired ErrorResponse errorResponse;

  public ResponseEntity<RestResponsePackageTypesDTO> getPackageTypes(
      Status status, String language) {
    List<PackageType> packageTypeList;
    switch (status) {
      case active:
        packageTypeList = packageTypeRepository.findByIsActive(Constant.TRUE);
        break;
      case disabled:
        packageTypeList = packageTypeRepository.findByIsActive(false);
        break;
      default:
        packageTypeList = packageTypeRepository.findAllByOrderByIsActiveDesc();
        break;
    }

    if (packageTypeList.isEmpty()) {
      log.info("There are no packageType available");
      errorResponse =
          new ErrorResponse(
              Constant.LM_0001,
              "PackageType not Available ",
              "There are no PackageType available",
              "There are no PackageType available");
      throw new MasterDataException(errorResponse);
    }

    List<PackageTypeDto> filteredPackageTypeList =
        packageTypeList.stream().map(PackageTypeDto::new).collect(Collectors.toList());

    RestResponsePackageTypesDTO packageTypes = new RestResponsePackageTypesDTO();
    packageTypes.setPackageTypes(filteredPackageTypeList);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(packageTypes);
  }

  public ResponseEntity<RestResponsePackageTypesDTO> getPackageTypes(
      PackageTypeIdentifier identifierType, String value, String language) {
    List<PackageType> packageTypeList = new ArrayList<>();

    if (value.isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (identifierType == PackageTypeIdentifier.name) {
      packageTypeList =
          packageTypeRepository.findAllByOrderByIsActiveDesc().stream()
              .filter(
                  packageType ->
                      StringUtils.containsIgnoreCase(packageType.getPackageTypeName(), value))
              .collect(Collectors.toList());
    } else {
      PackageType packageType = packageTypeRepository.findByPackageTypeCode(value);
      if (packageType != null) packageTypeList.add(packageType);
    }

    if (packageTypeList.isEmpty()) {
      log.info("There are no packageType available");
      errorResponse =
          new ErrorResponse(
              Constant.LM_0001,
              "PackageType not Available ",
              "There are no PackageType available",
              "There are no PackageType available");
      throw new MasterDataException(errorResponse);
    }
    List<PackageTypeDto> filteredPackageTypeList =
        packageTypeList.stream().map(PackageTypeDto::new).collect(Collectors.toList());

    RestResponsePackageTypesDTO packageTypes = new RestResponsePackageTypesDTO();
    packageTypes.setPackageTypes(filteredPackageTypeList);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(packageTypes);
  }

  public ResponseEntity<String> addPackageType(String packageTypeName, String language) {

    if (packageTypeName.isBlank()) {
      log.info("Invalid Package_type_name: ", packageTypeName);
      errorResponse =
          new ErrorResponse(
              Constant.LM_0003,
              "Invalid Package_type_name ",
              "Invalid Package_type_name  ",
              "Invalid Package_type_name ");
      throw new MasterDataException(errorResponse);
    }
    String packageTypeCode = packageTypeName.toLowerCase().trim();
    packageTypeCode = packageTypeCode.replace(' ', '-');
    PackageType packageType = packageTypeRepository.findByPackageTypeCode(packageTypeCode);
    if (packageType != null) {
      log.info("Package type already exists");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } else {
      PackageType newPackageType = new PackageType();
      newPackageType.setPackageTypeCode(packageTypeCode);
      newPackageType.setPackageTypeName(packageTypeName);
      packageTypeRepository.save(newPackageType);
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }

  public ResponseEntity<String> updatePackageType(PackageTypeDto request, String language) {
    if (StringUtils.isBlank(request.getPackageTypeCode())) {
      errorResponse =
          new ErrorResponse(
              Constant.LM_0003,
              "Invalid Package_type_code ",
              "Invalid Package_type_code  ",
              "Invalid Package_type_code ");
      throw new MasterDataException(errorResponse);
    }
    if (StringUtils.isBlank(request.getPackageTypeName()) && request.getIsActive() == null) {
      log.info("Package Type Code or Is Active is Mandatory ");
      errorResponse =
          new ErrorResponse(
              Constant.LM_0003,
              "Package Type Code or Is Active is Mandatory ",
              "Package Type Code or Is Active is Mandatory ",
              "Package Type Code or Is Active is Mandatory ");
      throw new MasterDataException(errorResponse);
    }
    PackageType packageType =
        packageTypeRepository.findByPackageTypeCode(request.getPackageTypeCode());
    if (packageType == null) {
      log.info(
          "There are no packageType found with PackageTypeCode: {}", request.getPackageTypeCode());
      errorResponse =
          new ErrorResponse(
              Constant.LM_0004,
              "There are no packageType found with PackageTypeName",
              "There are no packageType found with PackageTypeName",
              "There are no packageType found with PackageTypeName");
      throw new MasterDataException(errorResponse);
    } else {
      if (!StringUtils.isEmpty(request.getPackageTypeName())) {
        packageType.setPackageTypeName(request.getPackageTypeName());
      }
      if (request.getIsActive() != null) {
        packageType.setIsActive(request.getIsActive());
      }
      Date updatedDate = Calendar.getInstance().getTime();
      packageType.setUpdatedDateTime(updatedDate);
      packageTypeRepository.save(packageType);
      log.info("PackageType successfully updated");
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }
}
