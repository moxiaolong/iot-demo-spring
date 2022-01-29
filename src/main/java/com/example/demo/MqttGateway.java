package com.example.demo;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * mqtt网关
 *
 * @author dragon
 * @date 2022/01/29
 */
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
@Component
public interface MqttGateway {
    /**
     * 发送到mqtt
     *
     * @param data  数据
     * @param topic 主题
     */
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}