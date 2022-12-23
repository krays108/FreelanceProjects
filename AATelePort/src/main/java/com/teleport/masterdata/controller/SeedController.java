package com.teleport.masterdata.controller;

import com.teleport.masterdata.dto.planet.RestRequestDTO;
import com.teleport.masterdata.model.Planet;
import com.teleport.masterdata.model.Region;
import com.teleport.masterdata.model.SubRegion;
import com.teleport.masterdata.service.PlanetService;
import com.teleport.masterdata.service.SeederService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seeder")
@Slf4j
@Api(tags = {"Seeder APIS "})
public class SeedController {

    @Autowired
    SeederService seederService;

    @Autowired
    PlanetService planetService;

    @ApiOperation(value = "PlanetSeeder ", response = Planet.class)
    @PostMapping(value = "/planet")
    public void planetSeeder(@RequestParam("status") boolean status) throws Exception {
        log.info("Calling Planet Seeder services {}", status);
        if (status) {
            seederService.seedPlanet();
        }
    }

    @ApiOperation(value = "RegionSeeder ", response = Region.class)
    @PostMapping(value = "/region")
    public void regionSeeder(@RequestParam("status") boolean status) throws Exception {
        log.info("Calling Region Seeder services {}", status);
        if (status) {
            seederService.seedRegion();
        }
    }

    @ApiOperation(value = "SubRegionSeeder ", response = SubRegion.class)
    @PostMapping(value = "/sub-region")
    public void subRegionSeeder(@RequestParam("status") boolean status) throws Exception {
        log.info("Calling Sub Region Seeder services {}", status);
        if (status) {
            seederService.seedSubRegion();
        }
    }

    @ApiOperation(value = "IntermdiateRegionSeeder ", response = SubRegion.class)
    @PostMapping(value = "/intermediate-region")
    public void intermediateRegionSeeder(@RequestParam("status") boolean status) throws Exception {
        log.info("Calling intermediate Region Seeder services {}", status);
        if (status) {
            seederService.seedIntermediateRegion();
        }
    }

    @ApiOperation(value = "PackageTypeSeeder ", response = SubRegion.class)
    @PostMapping(value = "/package-type")
    public void packageTypeSeeder(@RequestParam("status") boolean status) throws Exception {
        log.info("Calling Package Type Seeder services {}", status);
        if (status) {
            seederService.seedPackageType();
        }
    }

    @ApiOperation(value = "VehicleTypeSeeder ", response = SubRegion.class)
    @PostMapping(value = "/vehicle-type")
    public void vehicleTypeSeeder(@RequestParam("status") boolean status) throws Exception {
        log.info("Calling Vehicle type Seeder services {}", status);
        if (status) {
            seederService.seedVehicleType();
        }
    }

    @ApiOperation(value = "ServiceTypeSeeder ", response = SubRegion.class)
    @PostMapping(value = "/service-type")
    public void serviceTypeSeeder(@RequestParam("status") boolean status) throws Exception {
        log.info("Calling Service type Seeder services {}", status);
        if (status) {
            seederService.seedServiceType();
        }
    }


}
