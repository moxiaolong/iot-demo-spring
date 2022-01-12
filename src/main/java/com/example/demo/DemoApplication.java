package com.example.demo;

import com.example.demo.config.InfluxDBConfig;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Random;

@SpringBootApplication
@RestController
public class DemoApplication {

    @Autowired
    private InfluxDBConfig influxDBConfig;

    @Autowired
    private MqttGateway mqttGateway;

    private final Random random =  new Random();

    @RequestMapping("/save")
    public Mono<Data> saveData(){
        //随机温度
        int temperature = random.nextInt(21) + 16;
        Data data = new Data();
        data.setSensorName("testSensor");
        data.setTemperature(temperature);
        HashMap<String, String> tagMap = new HashMap<>();
        tagMap.put("id","1");
        HashMap<String, Object> filedMap = new HashMap<>();
        filedMap.put("temperature",temperature);
        influxDBConfig.insert("temperature",tagMap,filedMap);
        mqttGateway.sendToMqtt(String.valueOf(temperature),"temperature");
        return Mono.just(data);
    }
    @RequestMapping("/queryResult")
    public Mono<QueryResult> getQueryResult(){
        QueryResult queryResult = influxDBConfig.query("SELECT MEAN(temperature) FROM temperature where tag=testSensor");
        return Mono.just(queryResult);
    }

    @RequestMapping("/testBlock")
    public Mono<String> testBlock() throws InterruptedException {
        Thread.sleep(3000L);
        return Mono.just("ok");
    }

    /**
     * 数据实体
     */
    private static class Data{
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
