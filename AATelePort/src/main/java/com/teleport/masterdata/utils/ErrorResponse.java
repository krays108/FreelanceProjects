package com.teleport.masterdata.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ErrorResponse {
    private String code;
    private String title;
    private String body;
    private String description;
//    private String httpStatus;


}
