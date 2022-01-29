package com.example.demo.config;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * db config influx
 *
 * @author dragon
 * @date 2022/01/29
 */
@Component
@Configuration
public class InfluxDBConfig {


    @Autowired
    private InfluxDB influxDB;


    /**
     * 插入
     *
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     * @param database    数据库
     */
    public void insert(String database, String measurement, Map<String, String> tags, Map<String, Object> fields) {
        Point.Builder builder = Point.measurement(measurement);
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        builder.tag(tags);
        builder.fields(fields);
        influxDB.write(database, "", builder.build());
    }

    /**
     * 查询
     *
     * @param command 查询语句
     */
    public QueryResult query(String database, String command) {
        return influxDB.query(new Query(command, database));
    }


}