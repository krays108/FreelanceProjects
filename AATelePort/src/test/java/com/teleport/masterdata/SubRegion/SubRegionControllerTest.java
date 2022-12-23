package com.teleport.masterdata.SubRegion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teleport.masterdata.AbstractTest;
import com.teleport.masterdata.dto.subregion.RestRequestSubRegionDTO;
import com.teleport.masterdata.dto.subregion.RestResponseSubRegionsDTO;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.model.Region;
import com.teleport.masterdata.model.SubRegion;
import com.teleport.masterdata.repository.PlanetRepository;
import com.teleport.masterdata.repository.RegionRepository;
import com.teleport.masterdata.repository.SubRegionRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SubRegionControllerTest extends AbstractTest {

  List<SubRegion> subRegionList;
  List<Region> regionList;
  List<Planet> planetList;

  String url = "/api/v1/sub-region";
  String urlSubRegions = "/api/v1/sub-regions";

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
        new SubRegion(
            "Northern Africa", "", "015", "62b19221cf38100e527f6d4b", regionList.get(0), true));
    subRegionList.add(
        new SubRegion(
            "Sub-Saharan Africa", "", "202", "62b19221cf38100e527f6d4b", regionList.get(0), true));
    subRegionList.add(
        new SubRegion(
            "Central Asia", "", "143", "62a054616dfe756962b61cd5", regionList.get(1), true));
    subRegionList.add(
        new SubRegion(
            "Central Asia", "", "030", "62a054616dfe756962b61cd5", regionList.get(1), true));
    subRegionList.add(
        new SubRegion(
            "Eastern Europe", "", "151", "62a0546e6dfe756962b61cd6", regionList.get(2), true));
    subRegionList.add(
        new SubRegion(
            "Northern Europe", "", "154", "62a0546e6dfe756962b61cd6", regionList.get(2), true));
  }

  @Test
  public void getSubRegionsByActiveStatus() throws Exception {
    when(subRegionRepository.findByIsActive(true))
        .thenReturn(
            subRegionList.stream().filter(SubRegion::getIsActive).collect(Collectors.toList()));
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlSubRegions)
                    .param("status", "active")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseSubRegionsDTO regions = super.mapFromJson(content, RestResponseSubRegionsDTO.class);
    assertTrue(regions.getSubRegions().size() > 0);
  }

  @Test
  public void getSubRegionsByDisabledStatus() throws Exception {
    when(subRegionRepository.findByIsActive(false))
        .thenReturn(
            subRegionList.stream()
                .filter(subRegion -> !subRegion.getIsActive())
                .collect(Collectors.toList()));

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlSubRegions)
                    .param("status", "disabled")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void getAllSubRegions() throws Exception {
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlSubRegions)
                    .param("status", "all")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseSubRegionsDTO regions = super.mapFromJson(content, RestResponseSubRegionsDTO.class);
    assertTrue(regions.getSubRegions().size() > 0);
  }

  @Test
  public void getEmptyStatusSubRegions() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlSubRegions)
                    .param("status", "")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getInvalidStatusSubRegions() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlSubRegions)
                    .param("status", "a")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getSubRegionWithM49Code() throws Exception {
    when(subRegionRepository.findByM49CodeOrderByIsActiveDesc("015"))
        .thenReturn(
            subRegionList.stream()
                .filter(subRegion -> Objects.equals(subRegion.getM49Code(), "015"))
                .collect(Collectors.toList()));

    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "m49")
                    .param("value", "015")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseSubRegionsDTO region = super.mapFromJson(content, RestResponseSubRegionsDTO.class);
    assertEquals(1, region.getSubRegions().size());
  }

  @Test
  public void getSubRegionWithRegionName() throws Exception {
    when(subRegionRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(
            subRegionList.stream()
                .filter(
                    subRegion -> StringUtils.containsIgnoreCase(subRegion.getSubRegionName(), "asia"))
                .collect(Collectors.toList()));

    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "name")
                    .param("value", "asia")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseSubRegionsDTO region = super.mapFromJson(content, RestResponseSubRegionsDTO.class);
    assertEquals(2, region.getSubRegions().size());
  }

  @Test
  public void getSubRegionWithM49Region() throws Exception {
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);
    when(regionRepository.findByM49Code("002")).thenReturn(regionList);

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
    RestResponseSubRegionsDTO regions = super.mapFromJson(content, RestResponseSubRegionsDTO.class);
    assertTrue(regions.getSubRegions().size() > 0);
  }

  @Test
  public void getSubRegionWithM49Planet() throws Exception {
    when(subRegionRepository.findAllByOrderByIsActiveDesc()).thenReturn(subRegionList);
    when(planetRepository.findByM49Code("001")).thenReturn(planetList);
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);

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
    RestResponseSubRegionsDTO regions = super.mapFromJson(content, RestResponseSubRegionsDTO.class);
    assertTrue(regions.getSubRegions().size() > 0);
  }

  @Test
  public void getRegionWithInvalidIdentifier() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "")
                    .param("value", "Africa")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getRegionWithInvalidName() throws Exception {
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
  public void updateSubRegionWithInvalidCode() throws Exception {

    RestRequestSubRegionDTO request = new RestRequestSubRegionDTO();
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
  public void updateSubRegionWithUnavailableCode() throws Exception {
    when(subRegionRepository.findByM49Code("999"))
        .thenReturn(
            subRegionList.stream()
                .filter(region -> region.getM49Code().equals("999"))
                .collect(Collectors.toList()));

    RestRequestSubRegionDTO request = new RestRequestSubRegionDTO();
    request.setCode("999");
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
    assertEquals(404, status);
  }

  @Test
  public void updateSubRegionWithValidCode() throws Exception {
    when(subRegionRepository.findByM49Code("015"))
        .thenReturn(
            subRegionList.stream()
                .filter(subRegion -> subRegion.getM49Code().equals("015"))
                .collect(Collectors.toList()));

    RestRequestSubRegionDTO request = new RestRequestSubRegionDTO();
    request.setCode("015");
    request.setIsActive(false);

    SubRegion subRegion =
        new SubRegion(
            "Northern Africa", "", "015", "62b19221cf38100e527f6d4b", regionList.get(0), true);
    when(subRegionRepository.save(subRegion)).thenReturn(subRegion);

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
