package com.teleport.masterdata.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.teleport.masterdata.utils.Constant;
import com.teleport.masterdata.utils.ErrorResponse;
import org.bson.json.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @Autowired ErrorResponse errorResponse;
  static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
/*
  static{
    InputStream inputStream = CustomExceptionHandler.class.getResourceAsStream("/ErrorResponses.yml");
    Yaml yaml = new Yaml();
    Map<String, Object> data = yaml.load(inputStream);
    System.out.println("Data Value :::::::::::"+data.toString());

//    ErrorResponse errorRes = getObject(inputStream.toString(), ErrorResponse.class);



  }*/

  public static <T> T getObject(String yaml, Class<T> clazz)
          throws JsonParseException, JsonMappingException, IOException {
    return mapper.readValue(yaml, clazz);

  }

  public static String getYAML(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }


  @ExceptionHandler(value = {MasterDataException.class})
  public ResponseEntity<Object> masterDataException(MasterDataException ex) {

    if (ex.getErrorResponse().getCode().equalsIgnoreCase(Constant.LM_0003)) {
      return new ResponseEntity<Object>(ex.getErrorResponse(), HttpStatus.BAD_REQUEST);
    } else {
      return new ResponseEntity<Object>(ex.getErrorResponse(), HttpStatus.NOT_FOUND);
    }
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> genericException(Exception ex) {
    return new ResponseEntity<Object>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
