package com.yj.support.consul;

import com.ecwid.consul.v1.ConsulClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author lbm
 * @Date 2019/10/3 4:54 下午
 * @Description 重写心跳Scheduler
 **/
public class YjTtlScheduler extends TtlScheduler {
    private static final Log log = LogFactory.getLog(YjTtlScheduler.class);
    private final Map<String, ScheduledFuture> serviceHeartbeats = new ConcurrentHashMap<>();
    private final TaskScheduler scheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
    private YjHeartbeatProperties configuration;
    private ConsulClient client;
    private String token;

    public YjTtlScheduler(YjHeartbeatProperties configuration, ConsulClient client, String token) {
        super(null, null);
        this.configuration = configuration;
        this.client = client;
        this.token = token;
    }

    @Override
    public void add(String instanceId) {
        ScheduledFuture task = this.scheduler.scheduleAtFixedRate(new YjTtlScheduler.ConsulHeartbeatTask(instanceId), this.configuration.computeHeartbeatInterval().toStandardDuration().getMillis());
        ScheduledFuture previousTask = (ScheduledFuture) this.serviceHeartbeats.put(instanceId, task);
        if (previousTask != null) {
            previousTask.cancel(true);
        }
        log.info("Add Consul heartbeat for: " + instanceId);
    }

    @Override
    public void remove(String instanceId) {
        ScheduledFuture task = (ScheduledFuture) this.serviceHeartbeats.get(instanceId);
        if (task != null) {
            task.cancel(true);
        }

        this.serviceHeartbeats.remove(instanceId);
    }

    private class ConsulHeartbeatTask implements Runnable {
        private String checkId;

        ConsulHeartbeatTask(String serviceId) {
            this.checkId = serviceId;
            if (!this.checkId.startsWith("service:")) {
                this.checkId = "service:" + this.checkId;
            }
        }

        @Override
        public void run() {
            //调用ConsulClient api 带token方法
            YjTtlScheduler.this.client.agentCheckPass(this.checkId, null, token);
            if (log.isDebugEnabled()) {
                log.debug("Sending consul heartbeat for: " + this.checkId + ", token: " + token);
            }
        }
    }
}