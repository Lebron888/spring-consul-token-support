package com.yj.support.consul;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClientConfiguration;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.cloud.consul.support.ConsulHeartbeatAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author luhaijun
 * @Description 重新定义YJConsulHeartbeatAutoConfiguration
 * @Date 2019/10/8 11:40 AM
 **/
@Configuration
@ConditionalOnConsulEnabled
@ConditionalOnProperty({"spring.cloud.consul.discovery.heartbeat.enabled"})
@ConditionalOnDiscoveryEnabled
@AutoConfigureBefore({ConsulServiceRegistryAutoConfiguration.class, ConsulHeartbeatAutoConfiguration.class})
@AutoConfigureAfter({ConsulDiscoveryClientConfiguration.class})
public class YjConsulHeartbeatAutoConfiguration {

    @Value("${spring.cloud.consul.discovery.acl-token:null}")
    private String token;

    public YjConsulHeartbeatAutoConfiguration() {
    }

    @Bean
    public YjHeartbeatProperties yjHeartbeatProperties() {
        return new YjHeartbeatProperties();
    }

    @Bean
    public TtlScheduler ttlScheduler(YjHeartbeatProperties yjHeartbeatProperties, ConsulClient consulClient) {
        return new YjTtlScheduler(yjHeartbeatProperties, consulClient, token);
    }
}
