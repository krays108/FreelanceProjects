package com.teleport.masterdata.repository;

import com.teleport.masterdata.model.IntermediateRegion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface IntermediateRegionRepository extends MongoRepository<IntermediateRegion, String> {

  @Query
  public List<IntermediateRegion> findByIsActive(boolean isActive);

  @Query
  public List<IntermediateRegion> findAllByOrderByIsActiveDesc();

  @Query
  public  List<IntermediateRegion> findByM49CodeOrderByIsActiveDesc(String m49Code);

  @Query
  public List<IntermediateRegion> findByIntermediateRegionCode(String intermediateRegionCode);
}
