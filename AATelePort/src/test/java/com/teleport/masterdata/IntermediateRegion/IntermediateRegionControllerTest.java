package com.teleport.masterdata.IntermediateRegion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teleport.masterdata.AbstractTest;
import com.teleport.masterdata.dto.intermediateregion.RestRequestIntermediateRegionDTO;
import com.teleport.masterdata.dto.intermediateregion.RestResponseIntermediateRegionsDTO;
import com.teleport.masterdata.model.IntermediateRegion;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.model.Region;
import com.teleport.masterdata.model.SubRegion;
import com.teleport.masterdata.repository.IntermediateRegionRepository;
import com.teleport.masterdata.repository.PlanetRepository;
import com.teleport.masterdata.repository.RegionRepository;
import com.teleport.masterdata.repository.SubRegionRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class IntermediateRegionControllerTest extends AbstractTest {

  List<IntermediateRegion> intermediateRegionList;
  List<SubRegion> subRegionList;
  List<Region> regionList;
  List<Planet> planetList;

  String url = "/api/v1/intermediate-region";
  String uriInterRegions = "/api/v1/intermediate-regions";

  @MockBean IntermediateRegionRepository intermediateRegionRepository;
  @MockBean SubRegionRepository subRegionRepository;
  @MockBean RegionRepository regionRepository;
  @MockBean PlanetRepository planetRepository;

  @Override
  @Before
  public void setUp() {
    super.setUp();

    planetList = new ArrayList<>();
    planetList.add(
        Planet.builder()
            .planet(new Planet("Earth", "SOL III", "001", "World", true))
            .id("62a0544a6dfe756962b61cd3")
            .build());

    regionList = new ArrayList<>();
    regionList.add(
        Region.builder()
            .region(new Region("Africa", "AF", "002", "62a0544a6dfe756962b61cd3", null, true))
            .id("62b19221cf38100e527f6d4b")
            .build());
    regionList.add(
        Region.builder()
            .region(new Region("Asia", "AS", "142", "62a0544a6dfe756962b61cd3", null, true))
            .id("62a054616dfe756962b61cd5")
            .build());
    regionList.add(
        Region.builder()
            .region(new Region("Europe", "EU", "150", "62a0544a6dfe756962b61cd3", null, true))
            .id("62a0546e6dfe756962b61cd6")
            .build());

    subRegionList = new ArrayList<>();
    subRegionList.add(
        SubRegion.builder()
            .subRegion(
                new SubRegion(
                    "Sub-Saharan Africa", "", "202", "62b19221cf38100e527f6d4b", null, true))
            .id("62b27cb9cf38100e527f6de0")
            .build());
    subRegionList.add(
        SubRegion.builder()
            .subRegion(
                new SubRegion("Northern Africa", "", "015", "62b19221cf38100e527f6d4b", null, true))
            .id("62b27b11cf38100e527f6dd4")
            .build());
    subRegionList.add(
        SubRegion.builder()
            .subRegion(
                new SubRegion(
                    "Central Asia", "", "143", "62a054616dfe756962b61cd5", regionList.get(1), true))
            .id("62b27dcecf38100e527f6de3")
            .build());
    subRegionList.add(
        SubRegion.builder()
            .subRegion(
                new SubRegion(
                    "Eastern Asia", "", "030", "62a054616dfe756962b61cd5", regionList.get(1), true))
            .id("62a054756dfe756962b61cf7")
            .build());
    subRegionList.add(
        SubRegion.builder()
            .subRegion(
                new SubRegion("Eastern Europe", "", "151", "62a0546e6dfe756962b61cd6", null, true))
            .id("62a2c13d126ff11c59f2561d")
            .build());
    subRegionList.add(
        SubRegion.builder()
            .subRegion(
                new SubRegion("Northern Europe", "", "154", "62a0546e6dfe756962b61cd6", null, true))
            .id("62a2c152126ff11c59f2561e")
            .build());

    intermediateRegionList = new ArrayList<>();
    intermediateRegionList.add(
        new IntermediateRegion(
            "Eastern Africa", "", "014", "62b27cb9cf38100e527f6de0", null, true));
    intermediateRegionList.add(
        new IntermediateRegion("Middle Africa", "", "017", "62b27cb9cf38100e527f6de0", null, true));
    intermediateRegionList.add(
        new IntermediateRegion(
            "Channel Islands", "", "830", "62a2c152126ff11c59f2561e", null, true));
  }

  @Test
  public void getIntermediateRegionsByActiveStatus() throws Exception {
    when(intermediateRegionRepository.findByIsActive(true))
        .thenReturn(
            intermediateRegionList.stream()
                .filter(IntermediateRegion::getIsActive)
                .collect(Collectors.toList()));
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uriInterRegions)
                    .param("status", "active")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseIntermediateRegionsDTO intermediateRegions =
        super.mapFromJson(content, RestResponseIntermediateRegionsDTO.class);
    assertTrue(intermediateRegions.getIntermediateRegions().size() > 0);
  }

  @Test
  public void getIntermediateRegionsByDisabledStatus() throws Exception {
    when(intermediateRegionRepository.findByIsActive(false))
        .thenReturn(
            intermediateRegionList.stream()
                .filter(intermediateRegion -> !intermediateRegion.getIsActive())
                .collect(Collectors.toList()));

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uriInterRegions)
                    .param("status", "disabled")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void getIntermediateRegionsByAllStatus() throws Exception {
    when(intermediateRegionRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(intermediateRegionList);
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uriInterRegions)
                    .param("status", "all")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseIntermediateRegionsDTO regions =
        super.mapFromJson(content, RestResponseIntermediateRegionsDTO.class);
    assertTrue(regions.getIntermediateRegions().size() > 0);
  }

  @Test
  public void getEmptyStatusIntermediateRegions() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uriInterRegions)
                    .param("status", "")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getInvalidStatusIntermediateRegions() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uriInterRegions)
                    .param("status", "s")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getIntermediateRegionWithM49Code() throws Exception {
    when(intermediateRegionRepository.findByM49CodeOrderByIsActiveDesc("014"))
        .thenReturn(
            intermediateRegionList.stream()
                .filter(
                    intermediateRegion -> Objects.equals(intermediateRegion.getM49Code(), "014"))
                .collect(Collectors.toList()));
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "m49")
                    .param("value", "014")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseIntermediateRegionsDTO regions =
        super.mapFromJson(content, RestResponseIntermediateRegionsDTO.class);
    assertEquals(1, regions.getIntermediateRegions().size());
  }

  @Test
  public void getIntermediateRegionByName() throws Exception {
    when(intermediateRegionRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(
            intermediateRegionList.stream()
                .filter(
                    intermediateRegion ->
                        StringUtils.containsIgnoreCase(
                            intermediateRegion.getIntermediateRegionName(), "channel"))
                .collect(Collectors.toList()));

    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "name")
                    .param("value", "channel")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseIntermediateRegionsDTO intermediateRegions =
        super.mapFromJson(content, RestResponseIntermediateRegionsDTO.class);
    assertEquals(1, intermediateRegions.getIntermediateRegions().size());
  }

  @Test
  public void getIntermediateRegionWithM49SubRegion() throws Exception {
    when(intermediateRegionRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(intermediateRegionList);
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);
    when(subRegionRepository.findByM49Code("202"))
        .thenReturn(
            subRegionList.stream()
                .filter(subRegion -> subRegion.getM49Code().equals("202"))
                .collect(Collectors.toList()));

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "m49_sub")
                    .param("value", "202")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseIntermediateRegionsDTO intermediateRegions =
        super.mapFromJson(content, RestResponseIntermediateRegionsDTO.class);
    assertTrue(intermediateRegions.getIntermediateRegions().size() > 0);
  }

  @Test
  public void getIntermediateRegionWithM49Region() throws Exception {
    when(intermediateRegionRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(intermediateRegionList);
    when(regionRepository.findByM49Code("002"))
        .thenReturn(
            regionList.stream()
                .filter(region -> region.getM49Code().equals("002"))
                .collect(Collectors.toList()));
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "m49_region")
                    .param("value", "002")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseIntermediateRegionsDTO intermediateRegions =
        super.mapFromJson(content, RestResponseIntermediateRegionsDTO.class);
    assertTrue(intermediateRegions.getIntermediateRegions().size() > 0);
  }

  @Test
  public void getIntermediateRegionWithM49Planet() throws Exception {
    when(intermediateRegionRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(intermediateRegionList);
    when(planetRepository.findByM49Code("001"))
        .thenReturn(
            planetList.stream()
                .filter(planet -> planet.getM49Code().equals("001"))
                .collect(Collectors.toList()));
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "m49_planet")
                    .param("value", "001")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseIntermediateRegionsDTO intermediateRegions =
        super.mapFromJson(content, RestResponseIntermediateRegionsDTO.class);
    assertTrue(intermediateRegions.getIntermediateRegions().size() > 0);
  }

  @Test
  public void getRegionWithInvalidIdentifier() throws Exception {

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "")
                    .param("value", "channel")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getRegionWithInvalidName() throws Exception {
    when(intermediateRegionRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(intermediateRegionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "name")
                    .param("value", "Arab")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void updateIntermediateRegionWithInvalidCode() throws Exception {
    RestRequestIntermediateRegionDTO request = new RestRequestIntermediateRegionDTO();
    request.setCode("");
    request.setIsActive(false);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.put(url)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void updateIntermediateRegionWithUnavailableCode() throws Exception {
    when(intermediateRegionRepository.findByIntermediateRegionCode("001"))
        .thenReturn(
            intermediateRegionList.stream()
                .filter(intermediateRegion -> intermediateRegion.getM49Code().equals("001"))
                .collect(Collectors.toList()));

    RestRequestIntermediateRegionDTO request = new RestRequestIntermediateRegionDTO();
    request.setCode("001");
    request.setIsActive(false);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.put(url)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void updateIntermediateRegionWithValidCode() throws Exception {
    when(intermediateRegionRepository.findByIntermediateRegionCode("014"))
        .thenReturn(
            intermediateRegionList.stream()
                .filter(intermediateRegion -> intermediateRegion.getM49Code().equals("014"))
                .collect(Collectors.toList()));

    RestRequestIntermediateRegionDTO request = new RestRequestIntermediateRegionDTO();
    request.setCode("014");
    request.setIsActive(true);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.put(url)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
  }
}
