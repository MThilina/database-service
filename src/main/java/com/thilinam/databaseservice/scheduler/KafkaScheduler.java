package com.thilinam.databaseservice.scheduler;

import com.thilinam.databaseservice.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KafkaScheduler {

   @Autowired private KafkaService kafkaService;

    private static Logger logger = LoggerFactory.getLogger(KafkaScheduler.class);

    /**
     * <p>
     *     According to the given scheduled time period kafka consumer will be executed
     *     currently scheduler executed accordingly 5 mins
     * </p>
     */
    @Scheduled(cron ="${schedule.time}")
    public void consumerScheduler(){
        logger.info("consumer scheduler started ....");
        kafkaService.initiate();
        kafkaService.consumeKafkaMessages();
        logger.info("consumer data provisioned & completed");
    }
}
