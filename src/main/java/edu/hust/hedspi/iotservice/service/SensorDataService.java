package edu.hust.hedspi.iotservice.service;

import edu.hust.hedspi.iotservice.entity.SensorDataEntity;
import edu.hust.hedspi.iotservice.repository.SensorDataRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;

    public SensorDataService(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    /**
     * Hàm lấy ra tất cả dữ liệu cảm biến
     * @return
     */
    public List<SensorDataEntity> getAll() {
        return sensorDataRepository.findAll();
    }

    public Integer create(SensorDataEntity input) {
        input.setId(null);
        SensorDataEntity savedEntity = sensorDataRepository.save(input);
        return savedEntity.getId();
    }

    public SensorDataEntity updateById(Integer id, SensorDataEntity input) throws NotFoundException {
        Optional<SensorDataEntity> optional = sensorDataRepository.findById(id);
        if (optional.isEmpty()) throw new NotFoundException("Sensor data id not found.");
        SensorDataEntity toSaveEntity = optional.get();
        if (!StringUtils.isEmpty(input.getName())) {
            toSaveEntity.setName(input.getName());
        }
        if (input.getValue() != null) {
            toSaveEntity.setValue(input.getValue());
        }
        if (input.getReceiveTime() != null) {
            toSaveEntity.setReceiveTime(input.getReceiveTime());
        }
        return sensorDataRepository.save(toSaveEntity);
    }

    public void deleteById(Integer id) throws NotFoundException {
        Optional<SensorDataEntity> optional = sensorDataRepository.findById(id);
        if (optional.isEmpty()) throw new NotFoundException("Sensor data id not found.");
        sensorDataRepository.deleteById(id);
    }
}
