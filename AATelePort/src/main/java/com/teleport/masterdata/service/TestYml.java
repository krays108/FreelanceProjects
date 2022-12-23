package com.teleport.masterdata.service;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class TestYml {


    public  void mainTest(){
        InputStream inputStream = getClass().getResourceAsStream("/ErrorResponses.yml");
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        System.out.println(data);
    }

    public static void main(String args){
        TestYml obj = new TestYml();
//        obj.maintTest();
    }
}
