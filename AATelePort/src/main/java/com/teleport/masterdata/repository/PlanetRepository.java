package com.teleport.masterdata.repository;

import com.teleport.masterdata.model.Planet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlanetRepository extends MongoRepository<Planet, String> {

  public List<Planet> findByIsActive(boolean status);

  public List<Planet> findAllByOrderByIsActiveDesc();

  public List<Planet> findByM49CodeOrderByIsActiveDesc(String m49Code);

  public List<Planet> findByNameRegexOrderByIsActiveDesc(String name);

  public List<Planet> findByM49Code(String m49Code);

  public Planet insert(Planet planet);
}
