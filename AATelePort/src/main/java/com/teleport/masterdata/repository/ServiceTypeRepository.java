package com.teleport.masterdata.repository;

import com.teleport.masterdata.model.ServiceType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ServiceTypeRepository extends MongoRepository<ServiceType, String> {

  @Query
  public List<ServiceType> findByServiceTypeCode(String serviceTypeCode);

  @Query
  public List<ServiceType> findByServiceTypeCodeOrderByIsActiveDesc(String serviceTypeCode);

  @Query
  public List<ServiceType> findByIsActive(boolean isActive);

  @Query
  public List<ServiceType> findAllByOrderByIsActiveDesc();
}
