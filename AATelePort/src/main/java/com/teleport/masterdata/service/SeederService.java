package com.teleport.masterdata.service;

import com.teleport.masterdata.model.*;
import com.teleport.masterdata.repository.*;
import com.teleport.masterdata.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SeederService {

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    SubRegionRepository subRegionRepository;

    @Autowired
    RegionService regionService;

    @Autowired
    IntermediateRegionService intermediateRegionService;

    @Autowired
    IntermediateRegionRepository intermediateRegionRepository;

    @Autowired
    PackageTypeRepository packageTypeRepository;

    @Autowired
    VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    ServiceTypeRepository serviceTypeRepository;

    public void seedPlanet() throws IOException, FileNotFoundException {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MasterData.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(6);
            log.info("seet name" + sheet.getSheetName());
            List<Planet> planetRowList = new ArrayList<Planet>();
            for (int rowData = 3; rowData <= sheet.getLastRowNum(); rowData++) {
                Planet planetRow = new Planet();
                XSSFRow dataRow = sheet.getRow(rowData);

                planetRow.setName(dataRow.getCell(0).getStringCellValue());
                planetRow.setM49Code(dataRow.getCell(1).getStringCellValue());
                planetRow.setM49Name(dataRow.getCell(2).getStringCellValue());
                planetRow.setIauCode(dataRow.getCell(3).getStringCellValue());
                planetRow.setIsActive(Constant.TRUE);
                Date date = Calendar.getInstance().getTime();
                planetRow.setCreatedDateTime(date);
                planetRow.setCreatedBy(Constant.SYSTEM);
                planetRowList.add(planetRow);
            }
            if (planetRowList.size() > 0) {
                planetRepository.insert(planetRowList);
                log.info(" Seeder Data Inserted into Planet ");
            } else {
                log.info(" Seeder Data Not Found ");
            }
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void seedRegion() throws IOException, FileNotFoundException {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MasterData.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(7);
            List<Region> regionRowList = new ArrayList<Region>();
            for (int rowData = 3; rowData <= sheet.getLastRowNum(); rowData++) {
                Region regionRow = new Region();

                XSSFRow dataRow = sheet.getRow(rowData);
                List<Planet> planetRecordList =
                        planetRepository.findByM49Code(dataRow.getCell(3).getStringCellValue());
                Planet planetRecord = planetRecordList.get(0);
                regionRow.setRegionName(dataRow.getCell(0).getStringCellValue());
                regionRow.setRegionCode(dataRow.getCell(1).getStringCellValue());
                regionRow.setM49Code(dataRow.getCell(2).getStringCellValue());
                regionRow.setPlanetId(planetRecord.getId());
                regionRow.setIsActive(Constant.TRUE);
                Date date = Calendar.getInstance().getTime();
                regionRow.setCreatedDateTime(date);
                regionRow.setCreatedBy(Constant.SYSTEM);
                regionRowList.add(regionRow);
            }
            regionRepository.insert(regionRowList);
            log.info(" Region Data Inserted into Region ");
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void seedSubRegion() throws IOException, FileNotFoundException {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MasterData.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(8);
            List<SubRegion> subRegionRowList = new ArrayList<SubRegion>();
            for (int rowData = 3; rowData <= sheet.getLastRowNum(); rowData++) {
                SubRegion subRegionRow = new SubRegion();

                XSSFRow dataRow = sheet.getRow(rowData);
                List<Region> regionList =
                        regionRepository.findByM49Code(dataRow.getCell(3).getStringCellValue());
                if (regionList.size() == 0) {
                    break;
                }
                Region regionRecord = regionList.get(0);
                subRegionRow.setSubRegionName(dataRow.getCell(0).getStringCellValue());
                subRegionRow.setSubRegionCode(dataRow.getCell(1).getStringCellValue());
                subRegionRow.setM49Code(dataRow.getCell(2).getStringCellValue());
                subRegionRow.setRegionId(regionRecord.getId());
                subRegionRow.setIsActive(Constant.TRUE);
                Date date = Calendar.getInstance().getTime();
                subRegionRow.setCreatedDateTime(date);
                subRegionRow.setCreatedBy(Constant.SYSTEM);
                subRegionRowList.add(subRegionRow);
            }
            subRegionRepository.insert(subRegionRowList);
            log.info(" Sub Region Data Inserted into SubRegion ");
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void seedIntermediateRegion() throws IOException, FileNotFoundException {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MasterData.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(9);
            List<IntermediateRegion> intermediateRegionRowList = new ArrayList<IntermediateRegion>();
            for (int rowData = 3; rowData <= sheet.getLastRowNum(); rowData++) {
                IntermediateRegion intermediateRegionRow = new IntermediateRegion();

                XSSFRow dataRow = sheet.getRow(rowData);
                List<SubRegion> subRegionList =
                        subRegionRepository.findByM49Code(dataRow.getCell(3).getStringCellValue());
                if (subRegionList.size() == 0) {
                    break;
                }
                SubRegion subRegionRecord = subRegionList.get(0);
                intermediateRegionRow.setIntermediateRegionName(dataRow.getCell(0).getStringCellValue());
                intermediateRegionRow.setIntermediateRegionCode(dataRow.getCell(1).getStringCellValue());
                intermediateRegionRow.setM49Code(dataRow.getCell(2).getStringCellValue());
                intermediateRegionRow.setSubRegionId(subRegionRecord.getId());
                intermediateRegionRow.setIsActive(Constant.TRUE);
                Date date = Calendar.getInstance().getTime();
                intermediateRegionRow.setCreatedDateTime(date);
                intermediateRegionRow.setCreatedBy(Constant.SYSTEM);
                intermediateRegionRowList.add(intermediateRegionRow);
            }
            intermediateRegionRepository.insert(intermediateRegionRowList);
            log.info(" Intermediate Region Data Inserted into IntermediateRegion ");
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void seedPackageType() throws IOException, FileNotFoundException {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MasterData.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(12);
            List<PackageType> vehicleTypeList = new ArrayList<PackageType>();
            for (int rowData = 3; rowData <= sheet.getLastRowNum(); rowData++) {
                PackageType packageTypeRow = new PackageType();

                XSSFRow dataRow = sheet.getRow(rowData);
                packageTypeRow.setPackageTypeName(dataRow.getCell(0).getStringCellValue());
                packageTypeRow.setPackageTypeCode(dataRow.getCell(1).getStringCellValue());
                packageTypeRow.setIsActive(Constant.TRUE);
                Date date = Calendar.getInstance().getTime();
                packageTypeRow.setCreatedDateTime(date);
                packageTypeRow.setCreatedBy(Constant.SYSTEM);
                vehicleTypeList.add(packageTypeRow);
            }
            packageTypeRepository.insert(vehicleTypeList);
            log.info(" Package Type Data Inserted into Package Type ");
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void seedVehicleType() throws IOException, FileNotFoundException {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MasterData.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(10);
            List<VehicleType> vehicleTypeList = new ArrayList<VehicleType>();
            for (int rowData = 3; rowData <= sheet.getLastRowNum(); rowData++) {
                VehicleType vehicleTypeRow = new VehicleType();

                XSSFRow dataRow = sheet.getRow(rowData);
                vehicleTypeRow.setVehicleTypeName(dataRow.getCell(0).getStringCellValue());
                vehicleTypeRow.setVehicleTypeCode(dataRow.getCell(1).getStringCellValue());
                vehicleTypeRow.setIsActive(Constant.TRUE);
                Date date = Calendar.getInstance().getTime();
                vehicleTypeRow.setCreatedDateTime(date);
                vehicleTypeRow.setCreatedBy(Constant.SYSTEM);
                vehicleTypeList.add(vehicleTypeRow);
            }
            vehicleTypeRepository.insert(vehicleTypeList);
            log.info(" Vehicle Type Data Inserted into Vehicle Type ");
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void seedServiceType() throws IOException, FileNotFoundException {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MasterData.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(11);
            List<ServiceType> serviceTypeList = new ArrayList<ServiceType>();
            for (int rowData = 3; rowData <= sheet.getLastRowNum(); rowData++) {
                ServiceType serviceTypeRow = new ServiceType();

                XSSFRow dataRow = sheet.getRow(rowData);
                serviceTypeRow.setServiceTypeName(dataRow.getCell(0).getStringCellValue());
                serviceTypeRow.setServiceTypeCode(dataRow.getCell(1).getStringCellValue());
                serviceTypeRow.setIsActive(Constant.TRUE);
                Date date = Calendar.getInstance().getTime();
                serviceTypeRow.setCreatedDateTime(date);
                serviceTypeRow.setCreatedBy(Constant.SYSTEM);
                serviceTypeList.add(serviceTypeRow);
            }
            serviceTypeRepository.insert(serviceTypeList);
            log.info(" Service Type Data Inserted into Service Type ");
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
