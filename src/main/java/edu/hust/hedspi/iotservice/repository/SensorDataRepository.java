package edu.hust.hedspi.iotservice.repository;

import edu.hust.hedspi.iotservice.entity.SensorDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorDataEntity, Integer> {
}
