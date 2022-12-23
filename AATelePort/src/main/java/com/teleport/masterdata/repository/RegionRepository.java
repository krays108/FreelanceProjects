package com.teleport.masterdata.repository;

import com.teleport.masterdata.model.Region;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RegionRepository extends MongoRepository<Region, String> {

  @Query
  public List<Region> findByIsActive(boolean isActive);

  @Query
  public List<Region> findAllByOrderByIsActiveDesc();

  @Query
  public List<Region> findByM49CodeOrderByIsActiveDesc(String m49Code);

  @Query
  public List<Region> findByRegionNameRegexOrderByIsActiveDesc(String regionName);

  @Query
  public List<Region> findByM49Code(String m49Code);
}
