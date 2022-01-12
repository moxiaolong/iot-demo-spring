package com.example.demo.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Configuration
public class InfluxDBConfig {

    @Value("${spring.influx.user}")
    private String userName;

    @Value("${spring.influx.password}")
    private String password;

    @Value("${spring.influx.url}")
    private String url;

    @Value("${spring.influx.database}")
    private String database;

    private InfluxDB influxDB;

    public InfluxDBConfig(){}

    public InfluxDBConfig(String userName, String password, String url, String database) {
        this.userName = userName;
        this.password = password;
        this.url = url;
        this.database = database;
        this.influxDB = influxDbBuild();
    }


    /**
     * 连接时序数据库；获得InfluxDB
     **/
    private InfluxDB influxDbBuild() {
        if (influxDB == null) {
            influxDB = InfluxDBFactory.connect(url, userName, password);
            influxDB.setDatabase(database);
        }
        return influxDB;
    }

    /**
     * 插入
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     */
    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        influxDbBuild();
        Point.Builder builder = Point.measurement(measurement);
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        builder.tag(tags);
        builder.fields(fields);
        influxDB.write(database, "", builder.build());
    }

    /**
     * 查询
     * @param command 查询语句
     */
    public QueryResult query(String command) {
        influxDbBuild();
        return influxDB.query(new Query(command, database));
    }


}