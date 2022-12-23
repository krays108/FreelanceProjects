package com.teleport.masterdata.service;

import com.teleport.masterdata.dto.servicetype.*;
import com.teleport.masterdata.model.ServiceType;
import com.teleport.masterdata.repository.ServiceTypeRepository;
import com.teleport.masterdata.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceTypeService {

  @Autowired ServiceTypeRepository serviceTypeRepository;

  public ResponseEntity<String> addServiceType(String serviceTypeName, String language) {
    if (serviceTypeName.isBlank()) {
      log.info("Invalid request");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    String code = serviceTypeName.toLowerCase().trim();
    code = code.replace(" ", "-");
    List<ServiceType> serviceTypes = serviceTypeRepository.findByServiceTypeCode(code);
    if (!serviceTypes.isEmpty()) {
      log.info("Service type already exists");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } else {
      ServiceType newServiceType = new ServiceType();
      String serviceTypeCode = serviceTypeName.toLowerCase().trim();
      serviceTypeCode = serviceTypeCode.replace(" ", "-");
      newServiceType.setServiceTypeName(serviceTypeName);
      newServiceType.setServiceTypeCode(serviceTypeCode);
      serviceTypeRepository.save(newServiceType);
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }

  public ResponseEntity<String> updateServiceType(ServiceTypeDTO request, String language) {
    if (StringUtils.isEmpty(request.getServiceTypeCode())
        || (StringUtils.isEmpty(request.getServiceTypeName()) && request.getIsActive() == null)) {
      log.info("Invalid request");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<ServiceType> serviceTypes =
        serviceTypeRepository.findByServiceTypeCode(request.getServiceTypeCode());
    if (serviceTypes.isEmpty()) {
      log.info(
          "Service type with service-type-code {} doesn't exist", request.getServiceTypeCode());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else if (serviceTypes.size() > 1) {
      log.info("Multiple entry exists for service-type-code {}", request.getServiceTypeCode());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } else {
      ServiceType serviceType = serviceTypes.get(0);
      if (!StringUtils.isEmpty(request.getServiceTypeName())) {
        serviceType.setServiceTypeName(request.getServiceTypeName());
      }
      if (request.getIsActive() != null) {
        serviceType.setIsActive(request.getIsActive());
      }
      Date updatedDate = Calendar.getInstance().getTime();
      serviceType.setUpdatedDateTime(updatedDate);
      serviceTypeRepository.save(serviceType);
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).build();
  }

  public ResponseEntity<RestResponseServiceTypesDTO> getServiceType(
      ServiceTypeIdentifier identifier, String value, String language) {
    List<ServiceTypeDTO> serviceTypeList;

    if (value.isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (identifier == ServiceTypeIdentifier.name) {
      serviceTypeList =
          serviceTypeRepository.findAllByOrderByIsActiveDesc().stream()
              .filter(
                  serviceType ->
                      StringUtils.containsIgnoreCase(serviceType.getServiceTypeName(), value))
              .map(ServiceTypeDTO::new)
              .collect(Collectors.toList());
    } else {
      serviceTypeList =
          serviceTypeRepository
              .findByServiceTypeCodeOrderByIsActiveDesc(value.toLowerCase())
              .stream()
              .map(ServiceTypeDTO::new)
              .collect(Collectors.toList());
    }

    if (serviceTypeList.isEmpty()) {
      log.info("There are no service-types found with {}: {}", identifier, value);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    RestResponseServiceTypesDTO serviceTypes = new RestResponseServiceTypesDTO();
    serviceTypes.setServiceTypes(serviceTypeList);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(serviceTypes);
  }

  public ResponseEntity<RestResponseServiceTypesDTO> getServiceTypes(
      Status status, String language) {
    List<ServiceTypeDTO> serviceTypeList;

    switch (status) {
      case active:
        serviceTypeList =
            serviceTypeRepository.findByIsActive(true).stream()
                .map(ServiceTypeDTO::new)
                .collect(Collectors.toList());
        break;
      case disabled:
        serviceTypeList =
            serviceTypeRepository.findByIsActive(false).stream()
                .map(ServiceTypeDTO::new)
                .collect(Collectors.toList());
        break;
      default:
        serviceTypeList =
            serviceTypeRepository.findAllByOrderByIsActiveDesc().stream()
                .map(ServiceTypeDTO::new)
                .collect(Collectors.toList());
        break;
    }

    if (serviceTypeList.isEmpty()) {
      log.info("There are no service-types available in {} status", status);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    RestResponseServiceTypesDTO serviceTypes = new RestResponseServiceTypesDTO();
    serviceTypes.setServiceTypes(serviceTypeList);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_LANGUAGE, language).body(serviceTypes);
  }
}
