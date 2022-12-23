package com.teleport.masterdata.Planet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teleport.masterdata.dto.planet.RestRequestDTO;
import com.teleport.masterdata.dto.planet.RestResponseDTO;
import com.teleport.masterdata.AbstractTest;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.repository.PlanetRepository;
import org.apache.commons.lang3.StringUtils;
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

public class PlanetControllerTest extends AbstractTest {

  List<Planet> planetList;

  String url = "/api/v1/planet";
  String urlPlanets = "/api/v1/planets";

  @MockBean PlanetRepository planetRepository;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    planetList = new ArrayList<>();
    planetList.add(new Planet("Earth", "SOL III", "001", "World", true));
    planetList.add(new Planet("Mars", "SOL IV", "002", "Mars", true));
    planetList.add(new Planet("Marss", "SOL V", "003", "Marss", true));
  }

  @Test
  public void getActivePlanets() throws Exception {
    when(planetRepository.findByIsActive(true))
        .thenReturn(planetList.stream().filter(Planet::getIsActive).collect(Collectors.toList()));

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlPlanets)
                    .param("status", "active")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseDTO planets = super.mapFromJson(content, RestResponseDTO.class);
    assertEquals(3, planets.getPlanets().size());
  }

  @Test
  public void getDisabledPlanets() throws Exception {
    when(planetRepository.findByIsActive(false))
        .thenReturn(
            planetList.stream()
                .filter(planet -> !planet.getIsActive())
                .collect(Collectors.toList()));

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlPlanets)
                    .param("status", "disabled")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void getAllPlanets() throws Exception {
    when(planetRepository.findAllByOrderByIsActiveDesc()).thenReturn(planetList);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlPlanets)
                    .param("status", "all")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseDTO planets = super.mapFromJson(content, RestResponseDTO.class);
    assertEquals(3, planets.getPlanets().size());
  }

  @Test
  public void getEmptyStatusPlanets() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlPlanets)
                    .param("status", "")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getInvalidStatusPlanets() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(urlPlanets)
                    .param("status", "a")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getPlanetWithM49Code() throws Exception {
    when(planetRepository.findByM49CodeOrderByIsActiveDesc("001"))
        .thenReturn(
            planetList.stream()
                .filter(planet -> planet.getM49Code() == "001")
                .collect(Collectors.toList()));
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "m49")
                    .param("value", "001")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseDTO planets = super.mapFromJson(content, RestResponseDTO.class);
    assertEquals(1, planets.getPlanets().size());
    assertEquals("Earth", planets.getPlanets().get(0).getName());
  }

  @Test
  public void getPlanetWithPlanetName() throws Exception {
    when(planetRepository.findAllByOrderByIsActiveDesc())
        .thenReturn(
            planetList.stream()
                .filter(planet -> StringUtils.containsIgnoreCase(planet.getName(), "mar"))
                .collect(Collectors.toList()));
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "name")
                    .param("value", "mar")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseDTO planets = super.mapFromJson(content, RestResponseDTO.class);
    assertEquals(2, planets.getPlanets().size());
  }

  @Test
  public void getPlanetInvalidIdentifier() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "")
                    .param("value", "Earth")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getPlanetWithInvalidName() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(url)
                    .param("identifier-type", "name")
                    .param("value", "Earths")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void updatePlanetWithInvalidCode() throws Exception {

    RestRequestDTO request = new RestRequestDTO();
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
  public void updatePlanetWithUnavailableCode() throws Exception {
    when(planetRepository.findByM49Code("004"))
        .thenReturn(
            planetList.stream()
                .filter(planet -> planet.getM49Code().equals("004"))
                .collect(Collectors.toList()));

    RestRequestDTO request = new RestRequestDTO();
    request.setCode("004");
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
  public void updatePlanetWithValidCode() throws Exception {
    when(planetRepository.findByM49Code("003"))
        .thenReturn(
            planetList.stream()
                .filter(planet -> planet.getM49Code().equals("003"))
                .collect(Collectors.toList()));

    RestRequestDTO request = new RestRequestDTO();
    request.setCode("003");
    request.setIsActive(false);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    Planet planet = new Planet("Marss", "SOL V", "003", "Marss", false);
    when(planetRepository.save(planet)).thenReturn(planet);
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
