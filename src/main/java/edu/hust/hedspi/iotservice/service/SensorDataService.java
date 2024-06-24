package edu.hust.hedspi.iotservice.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.hust.hedspi.iotservice.entity.SensorDataEntity;
import edu.hust.hedspi.iotservice.repository.SensorDataRepository;
import io.micrometer.common.util.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SensorDataService {
    private static final String MQTT_PUBLISHER_ID = "spring-server";
    private static final String MQTT_BROKER = "tcp://192.168.0.100:1883";
    private static final String USERNAME = "hieu";
    private static final String PASSWORD = "123456";
    private static final String MQTT_TOPIC = "home-sense";
    private final SensorDataRepository sensorDataRepository;

    public SensorDataService(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    @PostConstruct
    public void init() {
        subscribeToMqtt();
    }

    @Async
    public void subscribeToMqtt() {
        try {
            MqttClient client = new MqttClient(MQTT_BROKER, MQTT_PUBLISHER_ID);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    System.out.println("Message arrived: " + payload);
                    saveSensorData(payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used in subscriber
                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            client.connect(options);
            client.subscribe(MQTT_TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSensorData(String payload) {
        LocalDateTime now = LocalDateTime.now();
        Gson gson = new Gson();
        try {
            // Parse JSON string to JsonObject
            JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
            double temperature = jsonObject.get("temperature").getAsDouble();
            double humidity = jsonObject.get("humidity").getAsDouble();
            SensorDataEntity entity = new SensorDataEntity();
            entity.setReceiveTime(now);
            entity.setName("temperature");
            entity.setValue(temperature);
            sensorDataRepository.save(entity);
            entity = new SensorDataEntity();
            entity.setReceiveTime(now);
            entity.setName("humidity");
            entity.setValue(humidity);
            sensorDataRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SensorDataEntity> getAll() {
        return sensorDataRepository.findAll();
    }

    public List<SensorDataEntity> getAllByName(String name) {
        return sensorDataRepository.findAllByName(name);
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
