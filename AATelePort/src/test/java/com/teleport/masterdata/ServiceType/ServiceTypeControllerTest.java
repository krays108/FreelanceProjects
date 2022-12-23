package com.teleport.masterdata.ServiceType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teleport.masterdata.AbstractTest;
import com.teleport.masterdata.dto.servicetype.RestRequestAddServiceTypeDTO;
import com.teleport.masterdata.dto.servicetype.RestResponseServiceTypesDTO;
import com.teleport.masterdata.dto.servicetype.ServiceTypeDTO;
import com.teleport.masterdata.model.ServiceType;
import com.teleport.masterdata.repository.ServiceTypeRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ServiceTypeControllerTest extends AbstractTest {

  List<ServiceType> serviceTypeList;

  String url = "/api/v1/service-type";
  String urlGetByStatus = "/api/v1/service-types";

  @MockBean ServiceTypeRepository serviceTypeRepository;

  @Override
  @Before
  public void setUp() {
    super.setUp();

    serviceTypeList = new ArrayList<>();
    serviceTypeList.add(new ServiceType("Teleport Everywhere", "teleport-everywhere", true));
    serviceTypeList.add(new ServiceType("Teleport", "teleport", true));
    serviceTypeList.add(new ServiceType("Capital A ", "capital-a", true));
  }

  @Test
  public void addServiceTypeWithNoName() throws Exception {
    RestRequestAddServiceTypeDTO request = new RestRequestAddServiceTypeDTO();
    request.setServiceTypeName("");

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void addServiceTypeWithExistingCode() throws Exception {
    when(serviceTypeRepository.findByServiceTypeCode("teleport"))
        .thenReturn(
            serviceTypeList.stream()
                .filter(serviceType -> serviceType.getServiceTypeCode().equals("teleport"))
                .collect(Collectors.toList()));

    RestRequestAddServiceTypeDTO request = new RestRequestAddServiceTypeDTO();
    request.setServiceTypeName("Teleport");

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void addServiceTypeWithNewName() throws Exception {
    when(serviceTypeRepository.findByServiceTypeCode("capital-a-logistics"))
            .thenReturn(
                    serviceTypeList.stream()
                            .filter(serviceType -> serviceType.getServiceTypeCode().equals("capital-a-logistics"))
                            .collect(Collectors.toList()));

    RestRequestAddServiceTypeDTO request = new RestRequestAddServiceTypeDTO();
    request.setServiceTypeName("Capital A Logistics");

    ServiceType newServiceType = new ServiceType();
    newServiceType.setServiceTypeCode("capital-a-logistics");

    when(serviceTypeRepository.save(newServiceType)).thenReturn(newServiceType);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.post(url)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(requestJson)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
  }

  @Test
  public void updateServiceTypeWithInvalidCode() throws Exception {
    ServiceTypeDTO request = new ServiceTypeDTO();
    request.setServiceTypeCode("");

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
  public void updateServiceTypeWithValidCodeInvalidNameAndStatus() throws Exception {
    ServiceTypeDTO request = new ServiceTypeDTO();
    request.setServiceTypeCode("teleport");

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
  public void updateServiceTypeWithUnavailableCode() throws Exception {
    when(serviceTypeRepository.findByServiceTypeCode("aaa")).thenReturn(serviceTypeList.stream()
            .filter(serviceType -> serviceType.getServiceTypeCode().equals("aaa"))
            .collect(Collectors.toList()));

    ServiceTypeDTO request = new ServiceTypeDTO();
    request.setServiceTypeCode("aaa");
    request.setServiceTypeName("Aaa");

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
  public void updateServiceTypeWithTwoAvailableCode() throws Exception {
    when(serviceTypeRepository.findByServiceTypeCode("teleport")).thenReturn(serviceTypeList.stream()
            .filter(serviceType -> serviceType.getServiceTypeCode().contains("teleport"))
            .collect(Collectors.toList()));

    ServiceTypeDTO request = new ServiceTypeDTO();
    request.setServiceTypeCode("teleport");
    request.setServiceTypeName("Teleport");

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
  public void updateServiceTypeWithAvailableCode() throws Exception {
    when(serviceTypeRepository.findByServiceTypeCode("teleport")).thenReturn(serviceTypeList.stream()
            .filter(serviceType -> serviceType.getServiceTypeCode().equals("teleport"))
            .collect(Collectors.toList()));

    ServiceTypeDTO request = new ServiceTypeDTO();
    request.setServiceTypeCode("teleport");
    request.setServiceTypeName("Teleport One");
    request.setIsActive(true);

    ServiceType serviceType = new ServiceType("Teleport One", "teleport", true);
    serviceType.setUpdatedDateTime(Calendar.getInstance().getTime());

    when(serviceTypeRepository.save(serviceType)).thenReturn(serviceType);

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

  @Test
  public void getServiceTypeWithName() throws Exception {
    when(serviceTypeRepository.findAllByOrderByIsActiveDesc())
            .thenReturn(
                    serviceTypeList.stream()
                            .filter(serviceType -> StringUtils.containsIgnoreCase(serviceType.getServiceTypeName(), "Teleport"))
                            .collect(Collectors.toList()));

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(url)
                                    .param("identifier-type", "name")
                                    .param("value", "Teleport")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseServiceTypesDTO service = super.mapFromJson(content, RestResponseServiceTypesDTO.class);
    assertTrue(service.getServiceTypes().size()> 1);
  }

  @Test
  public void getServiceTypeWithCode() throws Exception {
    when(serviceTypeRepository.findByServiceTypeCodeOrderByIsActiveDesc("teleport"))
            .thenReturn(
                    serviceTypeList.stream()
                            .filter(serviceType -> serviceType.getServiceTypeCode().equals("teleport"))
                            .collect(Collectors.toList()));

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(url)
                                    .param("identifier-type", "code")
                                    .param("value", "teleport")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseServiceTypesDTO service = super.mapFromJson(content, RestResponseServiceTypesDTO.class);
    assertEquals(1, service.getServiceTypes().size());
  }

  @Test
  public void getServiceTypeWithInvalidIdentifier() throws Exception {
    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(url)
                                    .param("identifier-type", "")
                                    .param("value", "")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getServiceTypeWithInvalidName() throws Exception {
    when(serviceTypeRepository.findAllByOrderByIsActiveDesc())
            .thenReturn(
                    serviceTypeList.stream()
                            .filter(serviceType -> StringUtils.containsIgnoreCase(serviceType.getServiceTypeName(), "Air"))
                            .collect(Collectors.toList()));
    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(url)
                                    .param("identifier-type", "name")
                                    .param("value", "Air")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void getServiceTypesByActiveStatus() throws Exception {
    when(serviceTypeRepository.findByIsActive(true))
            .thenReturn(
                    serviceTypeList.stream().filter(ServiceType::getIsActive).collect(Collectors.toList()));

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlGetByStatus)
                                    .param("status", "active")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseServiceTypesDTO serviceTypes = super.mapFromJson(content, RestResponseServiceTypesDTO.class);
    assertTrue(serviceTypes.getServiceTypes().size() > 0);
  }

  @Test
  public void getServiceTypesByDisabledStatus() throws Exception {
    when(serviceTypeRepository.findByIsActive(true))
            .thenReturn(
                    serviceTypeList.stream().filter(serviceType -> !serviceType.getIsActive()).collect(Collectors.toList()));

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlGetByStatus)
                                    .param("status", "disabled")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void getAllServiceTypes() throws Exception {
    when(serviceTypeRepository.findAllByOrderByIsActiveDesc()).thenReturn(serviceTypeList);

    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlGetByStatus)
                                    .param("status", "all")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponseServiceTypesDTO serviceTypes = super.mapFromJson(content, RestResponseServiceTypesDTO.class);
    assertTrue(serviceTypes.getServiceTypes().size() > 0);
  }

  @Test
  public void getEmptyStatusServiceTypes() throws Exception {
    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlGetByStatus)
                                    .param("status", "")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getInvalidStatusServiceTypes() throws Exception {
    MvcResult mvcResult =
            mvc.perform(
                            MockMvcRequestBuilders.get(urlGetByStatus)
                                    .param("status", "a")
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }
}
