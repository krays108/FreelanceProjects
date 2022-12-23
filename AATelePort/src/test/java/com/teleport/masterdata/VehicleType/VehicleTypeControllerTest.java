package com.teleport.masterdata.VehicleType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teleport.masterdata.AbstractTest;
import com.teleport.masterdata.dto.vehicletype.*;
import com.teleport.masterdata.model.VehicleType;
import com.teleport.masterdata.repository.VehicleTypeRepository;
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

public class VehicleTypeControllerTest extends AbstractTest {

    List<VehicleType> vehicleTypeList;

    String url = "/api/v1/vehicle-type";
    String urlGetByStatus = "/api/v1/vehicle-types";

    @MockBean VehicleTypeRepository vehicleTypeRepository;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        vehicleTypeList = new ArrayList<>();
        vehicleTypeList.add(new VehicleType("1-ton lorry", "1-ton-lorry", true));
        vehicleTypeList.add(new VehicleType("2-ton-lorry", "2-ton-lorry", true));
        vehicleTypeList.add(new VehicleType("3-ton-lorry", "2-ton-lorry", true));
    }

    @Test
    public void getVehicleTypesByActiveStatus() throws Exception {
        when(vehicleTypeRepository.findByIsActive(true))
                .thenReturn(
                        vehicleTypeList.stream().filter(VehicleType::getIsActive).collect(Collectors.toList()));

        MvcResult mvcResult =
                mvc.perform(
                                MockMvcRequestBuilders.get(urlGetByStatus)
                                        .param("status", "active")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        RestResponseVehicleTypesDTO vehicleTypes = super.mapFromJson(content, RestResponseVehicleTypesDTO.class);
        assertTrue(vehicleTypes.getVehicleTypes().size() > 0);
    }

    @Test
    public void getVehicleTypesByDisabledStatus() throws Exception {
        when(vehicleTypeRepository.findByIsActive(true))
                .thenReturn(
                        vehicleTypeList.stream().filter(vehicleType -> !vehicleType.getIsActive()).collect(Collectors.toList()));

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
    public void getAllVehicleTypes() throws Exception {
        when(vehicleTypeRepository.findAllByOrderByIsActiveDesc()).thenReturn(vehicleTypeList);

        MvcResult mvcResult =
                mvc.perform(
                                MockMvcRequestBuilders.get(urlGetByStatus)
                                        .param("status", "all")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        RestResponseVehicleTypesDTO vehicleTypes = super.mapFromJson(content, RestResponseVehicleTypesDTO.class);
        assertTrue(vehicleTypes.getVehicleTypes().size() > 0);
    }

    @Test
    public void getEmptyStatusVehicleTypes() throws Exception {
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
    public void getInvalidStatusVehicleTypes() throws Exception {
        MvcResult mvcResult =
                mvc.perform(
                                MockMvcRequestBuilders.get(urlGetByStatus)
                                        .param("status", "a")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    public void getVehicleTypeWithName() throws Exception {
        when(vehicleTypeRepository.findAllByOrderByIsActiveDesc())
                .thenReturn(
                        vehicleTypeList.stream()
                                .filter(vehicleType -> StringUtils.containsIgnoreCase(vehicleType.getVehicleTypeName(), "lorry"))
                                .collect(Collectors.toList()));

        MvcResult mvcResult =
                mvc.perform(
                                MockMvcRequestBuilders.get(url)
                                        .param("identifier-type", "name")
                                        .param("value", "lorry")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        RestResponseVehicleTypesDTO vehicle = super.mapFromJson(content, RestResponseVehicleTypesDTO.class);
        assertTrue(vehicle.getVehicleTypes().size()> 1);
    }

    @Test
    public void getVehicleTypeWithCode() throws Exception {
        when(vehicleTypeRepository.findByVehicleTypeCodeOrderByIsActiveDesc("1-ton-lorry"))
                .thenReturn(
                        vehicleTypeList.stream()
                                .filter(vehicleType -> vehicleType.getVehicleTypeCode().equals("1-ton-lorry"))
                                .collect(Collectors.toList()));

        MvcResult mvcResult =
                mvc.perform(
                                MockMvcRequestBuilders.get(url)
                                        .param("identifier-type", "code")
                                        .param("value", "1-ton-lorry")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        RestResponseVehicleTypesDTO vehicle = super.mapFromJson(content, RestResponseVehicleTypesDTO.class);
        assertEquals(1, vehicle.getVehicleTypes().size());
    }

    @Test
    public void getVehicleTypeWithInvalidIdentifier() throws Exception {
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
    public void getVehicleTypeWithInvalidName() throws Exception {
        when(vehicleTypeRepository.findAllByOrderByIsActiveDesc())
                .thenReturn(
                        vehicleTypeList.stream()
                                .filter(vehicleType -> StringUtils.containsIgnoreCase(vehicleType.getVehicleTypeName(), "Air"))
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
    public void addVehicleTypeWithNoName() throws Exception {
        RestRequestAddVehicleTypeDTO request = new RestRequestAddVehicleTypeDTO();
        request.setVehicleType("");

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
    public void addVehicleTypeWithExistingCode() throws Exception {
        when(vehicleTypeRepository.findByVehicleTypeCode("1-ton-lorry"))
                .thenReturn(
                        vehicleTypeList.stream()
                                .filter(vehicleType -> vehicleType.getVehicleTypeCode().equals("1-ton-lorry"))
                                .collect(Collectors.toList()));

        RestRequestAddVehicleTypeDTO request = new RestRequestAddVehicleTypeDTO();
        request.setVehicleType("1-ton Lorry");

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
    public void addVehicleTypeWithNewName() throws Exception {
        when(vehicleTypeRepository.findByVehicleTypeCode("truck"))
                .thenReturn(
                        vehicleTypeList.stream()
                                .filter(vehicleType -> vehicleType.getVehicleTypeCode().equals("truck"))
                                .collect(Collectors.toList()));

        RestRequestAddVehicleTypeDTO request = new RestRequestAddVehicleTypeDTO();
        request.setVehicleType("Truck");

        VehicleType newVehicleType = new VehicleType();
        newVehicleType.setVehicleTypeCode("truck");

        when(vehicleTypeRepository.save(newVehicleType)).thenReturn(newVehicleType);

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
    public void updateVehicleTypeWithInvalidCode() throws Exception {
        VehicleTypeDTO request = new VehicleTypeDTO();
        request.setVehicleTypeCode("");

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
    public void updateVehicleTypeWithValidCodeInvalidNameAndStatus() throws Exception {
        VehicleTypeDTO request = new VehicleTypeDTO();
        request.setVehicleTypeCode("1-ton-lorry");

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
    public void updateVehicleTypeWithUnavailableCode() throws Exception {
        when(vehicleTypeRepository.findByVehicleTypeCode("aaa")).thenReturn(vehicleTypeList.stream()
                .filter(vehicleType -> vehicleType.getVehicleTypeCode().equals("aaa"))
                .collect(Collectors.toList()));

        VehicleTypeDTO request = new VehicleTypeDTO();
        request.setVehicleTypeCode("aaa");
        request.setVehicleType("Aaa");

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
    public void updateVehicleTypeWithTwoAvailableCode() throws Exception {
        when(vehicleTypeRepository.findByVehicleTypeCode("lorry")).thenReturn(vehicleTypeList.stream()
                .filter(vehicleType -> vehicleType.getVehicleTypeCode().contains("lorry"))
                .collect(Collectors.toList()));

        VehicleTypeDTO request = new VehicleTypeDTO();
        request.setVehicleTypeCode("lorry");
        request.setVehicleType("Lorry");

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
    public void updateVehicleTypeWithAvailableCode() throws Exception {
        when(vehicleTypeRepository.findByVehicleTypeCode("1-ton-lorry")).thenReturn(vehicleTypeList.stream()
                .filter(vehicleType -> vehicleType.getVehicleTypeCode().equals("1-ton-lorry"))
                .collect(Collectors.toList()));

        VehicleTypeDTO request = new VehicleTypeDTO();
        request.setVehicleTypeCode("1-ton-lorry");
        request.setVehicleType("1-Ton Lorry");
        request.setIsActive(true);

        VehicleType vehicleType = new VehicleType("1-Ton Lorry", "1-ton-lorry", true);
        vehicleType.setUpdatedDateTime(Calendar.getInstance().getTime());

        when(vehicleTypeRepository.save(vehicleType)).thenReturn(vehicleType);

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
