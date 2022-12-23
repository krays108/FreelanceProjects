package com.teleport.masterdata.repository;

import com.teleport.masterdata.model.PackageType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PackageTypeRepository extends MongoRepository<PackageType, String> {

  public PackageType insert(PackageType packageType);

  public PackageType findByPackageTypeCode(String packageTypeCode);

  public List<PackageType> findByPackageTypeName(String packageTypeName);

  public List<PackageType> findByIsActive(boolean status);

  public List<PackageType> findAllByOrderByIsActiveDesc();
}
