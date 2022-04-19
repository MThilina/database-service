package com.thilinam.databaseservice.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "device_data")
public class DeviceData {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "device_id",nullable = false)
    private String deviceId;

    @Column(name = "sensor_id",nullable = false)
    private String sensorId;

    @Column(name = "sensor_reading", nullable = false)
    private String sensorReading;

    @Column(name = "event_time_stamp",nullable = false)
    private String eventTimeStamp;

    @Column(name = "topic_name",nullable = false)
    private String topicName;
}
