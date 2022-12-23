package com.teleport.masterdata.repository;

import com.teleport.masterdata.model.SubRegion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SubRegionRepository extends MongoRepository<SubRegion, String> {

  @Query
  public List<SubRegion> findByIsActive(boolean isActive);

  @Query
  public List<SubRegion> findAllByOrderByIsActiveDesc();

  @Query
  public List<SubRegion> findByM49CodeOrderByIsActiveDesc(String value);

  @Query
  public List<SubRegion> findByM49Code(String m49Code);
}
