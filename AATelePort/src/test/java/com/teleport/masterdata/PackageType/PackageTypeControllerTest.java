package com.teleport.masterdata.PackageType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teleport.masterdata.AbstractTest;
import com.teleport.masterdata.dto.packagetype.RestResponsePackageTypesDTO;
import com.teleport.masterdata.model.PackageType;
import com.teleport.masterdata.repository.PackageTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PackageTypeControllerTest extends AbstractTest {

  List<PackageType> packageTypeList;

  @MockBean PackageTypeRepository packageTypeRepository;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    packageTypeList = new ArrayList<>();
    packageTypeList.add(
        new PackageType("Welcome to package Type Code ", "welcome-to-package-type-code", true));
    packageTypeList.add(
        new PackageType(
            "Welcome to package Type Code test ", "welcome-to-package-type-code-test", false));
    packageTypeList.add(
        new PackageType(
            "welcome to package type code south",
            "professionla-service-is-provided-all-over-south-indi",
            true));
  }

  @Test
  public void getActivePackageType() throws Exception {
    when(packageTypeRepository.findByIsActive(true))
        .thenReturn(
            packageTypeList.stream().filter(PackageType::getIsActive).collect(Collectors.toList()));

    String uri = "/api/v1/package-typesByStatus";
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uri)
                    .param("status", "active")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponsePackageTypesDTO packageTypeData = super.mapFromJson(content, RestResponsePackageTypesDTO.class);
    assertEquals(packageTypeData.getPackageTypes().size(), 2);
  }

  @Test
  public void getDisabledPackageType() throws Exception {
    when(packageTypeRepository.findByIsActive(false))
        .thenReturn(
            packageTypeList.stream()
                .filter(packageType -> !packageType.getIsActive())
                .collect(Collectors.toList()));

    String uri = "/api/v1/package-typesByStatus";
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uri)
                    .param("status", "disabled")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponsePackageTypesDTO packageTypeData = super.mapFromJson(content, RestResponsePackageTypesDTO.class);
    assertEquals(packageTypeData.getPackageTypes().size(), 1);
  }

  @Test
  public void getAllPackageType() throws Exception {
    when(packageTypeRepository.findAllByOrderByIsActiveDesc()).thenReturn(packageTypeList);

    String uri = "/api/v1/package-typesByStatus";
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uri)
                    .param("status", "all")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
    String content = mvcResult.getResponse().getContentAsString();
    RestResponsePackageTypesDTO packageTypeData = super.mapFromJson(content, RestResponsePackageTypesDTO.class);
    assertEquals(packageTypeData.getPackageTypes().size(), 3);
  }

  @Test
  public void getEmptyStatusPackageType() throws Exception {
    String uri = "/api/v1/package-typesByStatus";
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uri)
                    .param("status", "")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getPackageTypeInvalidIdentifier() throws Exception {
    String uri = "/api/v1/package-types";
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uri)
                    .param("identifier-type", "")
                    .param("value", "Earth")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void getPackageTypeWithInvalidName() throws Exception {
    String uri = "/api/v1/package-types";
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(uri)
                    .param("identifier-type", "testname")
                    .param("value", "Earths")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void updatePackageTypeWithInvalidCode() throws Exception {
    String uri = "/api/v1/package";

    PackageType request = new PackageType();
    request.setPackageTypeName("");
    request.setIsActive(false);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.put(uri)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void updatePackageTypetWithUnavailableCode() throws Exception {
    when(packageTypeRepository.findByPackageTypeName("Welcome to package Type Code "))
        .thenReturn(
            packageTypeList.stream()
                .filter(
                    packageType ->
                        packageType.getPackageTypeName().equals("Welcome to package Type Code "))
                .collect(Collectors.toList()));
    String uri = "/api/v1/package";

    PackageType request = new PackageType();
    request.setPackageTypeName("Welcome to package Type Code ");
    request.setPackageTypeCode("Welcome-to-package-Type-Code");
    request.setIsActive(true);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.put(uri)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(404, status);
  }

  @Test
  public void updatePackageTypeWithValidCode() throws Exception {
    when(packageTypeRepository.findByPackageTypeName("Welcome to package Type Code "))
        .thenReturn(
            packageTypeList.stream()
                .filter(
                    packageType ->
                        packageType.getPackageTypeName().equals("Welcome to package Type Code "))
                .collect(Collectors.toList()));

    String uri = "/api/v1/package";

    PackageType request = new PackageType();
    request.setPackageTypeName("Welcome to package Type Code ");
    request.setPackageTypeCode("welcome-to-package-type-code");
    request.setIsActive(true);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    when(packageTypeRepository.findByPackageTypeCode("welcome-to-package-type-code"))
        .thenReturn(request);
    when(packageTypeRepository.save(request)).thenReturn(request);
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.put(uri)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
  }

  @Test
  public void createPackageTypetWithInvalidCode() throws Exception {
    when(packageTypeRepository.findByPackageTypeName("Welcome to package Type  "))
        .thenReturn(
            packageTypeList.stream()
                .filter(
                    packageType ->
                        packageType.getPackageTypeName().equals("Welcome to package Type Code "))
                .collect(Collectors.toList()));
    String uri = "/api/v1/package-type";

    PackageType request = new PackageType();
    request.setPackageTypeName("");
    request.setPackageTypeCode("Welcome-to-package-Type-Code");
    request.setIsActive(true);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(400, status);
  }

  @Test
  public void createPackageTypeWithValidCode() throws Exception {
    when(packageTypeRepository.findByPackageTypeName("Welcome to package Type Code "))
        .thenReturn(
            packageTypeList.stream()
                .filter(
                    packageType ->
                        packageType.getPackageTypeName().equals("Welcome to package Type Code "))
                .collect(Collectors.toList()));

    String uri = "/api/v1/package-type";

    PackageType request = new PackageType();
    request.setPackageTypeName("Welcome to package Type Code ");
    request.setPackageTypeCode("welcome-to-package-type-code");
    request.setIsActive(true);

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    when(packageTypeRepository.insert(request)).thenReturn(request);
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(200, status);
  }
}
