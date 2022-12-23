package com.teleport.masterdata.Region;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import com.teleport.masterdata.AbstractTest;
import com.teleport.masterdata.dto.region.RestRequestRegionDTO;
import com.teleport.masterdata.dto.region.RestResponseRegionsDTO;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.model.Region;
import com.teleport.masterdata.repository.RegionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RegionControllerTest extends AbstractTest {

  List<Region> regionList;

  Planet planet = new Planet("Earth", "SOL III", "001", "World", true);

  String url = "/api/v1/regions";
  String urlToUpdate = "/api/v1/region";

  @MockBean
  RegionRepository regionRepository;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    regionList = new ArrayList<>();
    regionList.add(new Region("Africa", "AF", "002", "62a0544a6dfe756962b61cd3",planet, true));
    regionList.add(new Region("Asia", "AS", "142", "62a0544a6dfe756962b61cd3",planet, true));
    regionList.add(new Region("Europe", "EU", "150", "62a0544a6dfe756962b61cd3", planet,true));

  }

  @Test
  public void getActiveRegions() throws Exception {
    when(regionRepository.findByIsActive(true)).thenReturn(regionList.stream().filter(Region::getIsActive).collect(Collectors.toList()));

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("status", "active")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseRegionsDTO regions = super.mapFromJson(content, RestResponseRegionsDTO.class);
    assertTrue(regions.getRegions().size() > 0);
  }

  @Test
  public void getDisabledRegions() throws Exception {
    when(regionRepository.findByIsActive(false)).thenReturn(regionList.stream().filter(region -> !region.getIsActive()).collect(Collectors.toList()));

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("status", "disabled")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void getAllRegions() throws Exception {
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("status", "all")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseRegionsDTO regions = super.mapFromJson(content, RestResponseRegionsDTO.class);
    assertEquals(3, regions.getRegions().size());
  }

  @Test
  public void getEmptyStatusRegions() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("status", "")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getInvalidStatusRegions() throws Exception {
    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(url)
                                    .param("status", "a")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getRegionWithM49Code() throws Exception {
    when(regionRepository.findByM49CodeOrderByIsActiveDesc("002")).thenReturn(regionList.stream().filter(region -> region.getM49Code() == "002").collect(Collectors.toList()));

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlToUpdate)
                                    .param("identifier-type", "m49")
                                    .param("value", "002")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseRegionsDTO region = super.mapFromJson(content, RestResponseRegionsDTO.class);
    assertEquals(1, region.getRegions().size());
  }

  @Test
  public void getRegionWithRegionName() throws Exception {
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList.stream().filter(region -> StringUtils.containsIgnoreCase(region.getRegionName(), "asia")).collect(Collectors.toList()));

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlToUpdate)
                                    .param("identifier-type", "name")
                                    .param("value", "asia")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseRegionsDTO region = super.mapFromJson(content, RestResponseRegionsDTO.class);
    assertEquals(1, region.getRegions().size());
  }

  @Test
  public void getRegionWithM49Planet() throws Exception {
    when(regionRepository.findAllByOrderByIsActiveDesc()).thenReturn(regionList);

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlToUpdate)
                                    .param("identifier-type", "m49_planet")
                                    .param("value", "001")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseRegionsDTO regions = super.mapFromJson(content, RestResponseRegionsDTO.class);
    assertTrue(regions.getRegions().size() > 0);
  }

  @Test
  public void getRegionWithInvalidIdentifier() throws Exception {
    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlToUpdate)
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
                            MockMvcRequestBuilders.get(urlToUpdate)
                                    .param("identifier-type", "name")
                                    .param("value", "Arab")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void updateRegionWithInvalidCode() throws Exception {

    RestRequestRegionDTO request = new RestRequestRegionDTO();
    request.setCode("");
    request.setIsActive(false);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.put(urlToUpdate)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestJson)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void updateRegionWithUnavailableCode() throws Exception {
    when(regionRepository.findByM49Code("999")).thenReturn(regionList.stream().filter(region -> region.getM49Code().equals("999")).collect(Collectors.toList()));

    RestRequestRegionDTO request = new RestRequestRegionDTO();
    request.setCode("999");
    request.setIsActive(true);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.put(urlToUpdate)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestJson)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void updateRegionWithValidCode() throws Exception {
    when(regionRepository.findByM49Code("002")).thenReturn(regionList.stream().filter(region -> region.getM49Code().equals("002")).collect(Collectors.toList()));

    RestRequestRegionDTO request = new RestRequestRegionDTO();
    request.setCode("002");
    request.setIsActive(false);

    Region region = new Region("Africa", "AF", "002", "62a0544a6dfe756962b61cd3",planet, false);
    when(regionRepository.save(region)).thenReturn(region);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.put(urlToUpdate)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestJson)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
  }
}
