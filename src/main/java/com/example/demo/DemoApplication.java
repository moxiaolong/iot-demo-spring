package com.example.demo;

import com.example.demo.config.InfluxDBConfig;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * 演示应用程序
 *
 * @author dragon
 * @date 2022/01/29
 */
@SpringBootApplication
@RestController
@Slf4j
public class DemoApplication {

    @Autowired
    private InfluxDBConfig influxDBConfig;

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Random random = new Random();

    @GetMapping("/save")
    public Data saveData() {
        //随机温度
        int temperature = random.nextInt(21) + 16;
        Data data = new Data();
        data.setSensorName("testSensor");
        data.setTemperature(temperature);
        HashMap<String, String> tagMap = new HashMap<>();
        tagMap.put("id", "1");
        HashMap<String, Object> filedMap = new HashMap<>();
        filedMap.put("temperature", temperature);

        //下面三个操作都是阻塞的，变成非阻塞

        //保存至Influx
        influxDBConfig.insert("test", "temperature", tagMap, filedMap);
        //发送至MQ
        mqttGateway.sendToMqtt(String.valueOf(temperature), "temperature");
        //保存至SqlLite
        jdbcTemplate.update("update temperature_data set temperature=" + temperature + " where id =1");
        return data;

    }

    @GetMapping("/queryResult")
    public QueryResult getQueryResult() {
        return influxDBConfig.query("test", "SELECT MEAN(temperature) FROM \"temperature\" WHERE time > now() - 20m");
    }

    @GetMapping("/testBlock")
    public String testBlock() throws ExecutionException, InterruptedException {
        log.info("testBlock in");
        try {
            Thread.sleep(3000L);
            log.info("testBlock result");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";

    }

    /**
     * 数据实体
     */
    private static class Data {
        /**
         * 温度
         */
        private int temperature;
        /**
         * 传感器名
         */
        private String sensorName;

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public String getSensorName() {
            return sensorName;
        }

        public void setSensorName(String sensorName) {
            this.sensorName = sensorName;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


}
