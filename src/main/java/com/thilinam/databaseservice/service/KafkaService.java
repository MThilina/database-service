package com.thilinam.databaseservice.service;

import com.google.gson.Gson;
import com.thilinam.databaseservice.entity.DeviceData;
import com.thilinam.databaseservice.repository.DeviceDataRepository;
import com.thilinam.kafka.KafkaConsumerDetail;
import com.thilinam.model.DeviceBasedData;
import com.thilinam.model.SensorBasedData;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaService {

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    @Value("${kafka.polling.time-out}")
    long maxPoolingTimeOut;

    @Value("${kafka.stopping.threshold}")
    int maxThreshHoldStopExecution;

    @Value("${kafka.consumer.topic}")
    String subscribedTopic;

    private static Logger logger = LoggerFactory.getLogger(KafkaService.class);
    KafkaConsumerDetail consumerDetail;

    public void initiate() {
        consumerDetail = KafkaConsumerDetail.getKafkaConsumerInstance();
    }

    public void consumeKafkaMessages() {

        Consumer consumer = consumerDetail.subscribeConsumer(subscribedTopic);
        Gson gson = new Gson();

        final int stoppingThreshHold = maxThreshHoldStopExecution;
        int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<String, String> consumerRecord = consumer.poll(maxPoolingTimeOut);

            // polling records from the consumer and circuit break if there are no consumer records until stoppingThreshHold
            if (consumerRecord.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > stoppingThreshHold) {
                    break;
                } else {
                    continue;
                }
            }


            for (ConsumerRecord<String, String> record : consumerRecord) {
                try {
                    synchronized (this) {
                        DeviceBasedData deviceBasedData = gson.fromJson(record.value(), DeviceBasedData.class);
                        List<SensorBasedData> sensorData = deviceBasedData.getSensorData(); // list of sensors and sensor related data
                        List<DeviceData> sensorDataForDevice = new ArrayList<>(); // list contains sensor data for device

                        sensorData.forEach(sensorDatum -> {
                            DeviceData deviceData = new DeviceData();
                            deviceData.setDeviceId(deviceBasedData.getDeviceId());
                            deviceData.setEventTimeStamp(deviceBasedData.getTimeStamp());
                            deviceData.setSensorId(sensorDatum.getSensorId());
                            deviceData.setTopicName(subscribedTopic);
                            deviceData.setSensorReading(sensorDatum.getSensorData().stream().findAny().get());
                            sensorDataForDevice.add(deviceData);
                        });

                        deviceDataRepository.saveAll(sensorDataForDevice);
                        logger.debug("successfully persisted ..");
                    }

                } catch (Exception e) {
                    logger.error("Error occurred :{}", e.getMessage());
                }
            }
            consumer.commitAsync();
        }
//        consumer.close(); // since returns a singelton object
    }
}
