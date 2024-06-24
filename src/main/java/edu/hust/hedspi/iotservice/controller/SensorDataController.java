package edu.hust.hedspi.iotservice.controller;

import edu.hust.hedspi.iotservice.entity.SensorDataEntity;
import edu.hust.hedspi.iotservice.service.SensorDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.NotFoundException;

@RestController
@RequestMapping("/api/SensorDatas")
@Api(tags = "Sensor data", description = "API sensor data management")
public class SensorDataController {
    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @GetMapping("")
    @ApiOperation("API get all sensor data")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(sensorDataService.getAll());
    }

    @PostMapping("")
    @ApiOperation("API create sensor data")
    public ResponseEntity<?> create(@RequestBody SensorDataEntity input) {
        if (input == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sensorDataService.create(input));
    }

    @PutMapping("/{id}")
    @ApiOperation("API update sensor data")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody SensorDataEntity input) {
        if (input == null) {
            return ResponseEntity.noContent().build();
        }
        int numId;
        SensorDataEntity updatedData;
        try {
            numId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Id is not an integer");
        }
        try {
            updatedData = sensorDataService.updateById(numId, input);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedData);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("API delete sensor data")
    public ResponseEntity<?> delete(@PathVariable String id) {
        int numId;
        try {
            numId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Id is not an integer");
        }
        try {
            sensorDataService.deleteById(numId);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
