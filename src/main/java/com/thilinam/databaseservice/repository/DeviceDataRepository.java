package com.thilinam.databaseservice.repository;

import com.thilinam.databaseservice.entity.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceDataRepository extends JpaRepository<DeviceData,Long> {
}
