package edu.hust.hedspi.iotservice.repository;

import edu.hust.hedspi.iotservice.entity.SensorDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorDataEntity, Integer> {
    @Query("select s from SensorDataEntity s where s.name = ?1 order by s.receiveTime desc limit 1000")
    List<SensorDataEntity> findAllByName(String name);
}
