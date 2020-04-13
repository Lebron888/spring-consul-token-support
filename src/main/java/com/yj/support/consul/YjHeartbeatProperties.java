package com.yj.support.consul;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Period;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(
        prefix = "spring.cloud.consul.discovery.heartbeat"
)
@Validated
public class YjHeartbeatProperties {
    private static final Log log = LogFactory.getLog(YjHeartbeatProperties.class);
    boolean enabled = false;
    @Min(1L)
    private int ttlValue = 30;
    @NotNull
    private String ttlUnit = "s";
    @DecimalMin("0.1")
    @DecimalMax("0.9")
    private double intervalRatio = 0.6666666666666666D;

    public YjHeartbeatProperties() {
    }

    protected Period computeHeartbeatInterval() {
        double interval = (double) this.ttlValue * this.intervalRatio;
        double max = Math.max(interval, 1.0D);
        int ttlMinus1 = this.ttlValue - 1;
        double min = Math.min((double) ttlMinus1, max);
        Period heartbeatInterval = new Period(Math.round(1000.0D * min));
        log.debug("Computed heartbeatInterval: " + heartbeatInterval);
        return heartbeatInterval;
    }

    public String getTtl() {
        return this.ttlValue + this.ttlUnit;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Min(1L)
    public int getTtlValue() {
        return this.ttlValue;
    }

    public void setTtlValue(@Min(1L) int ttlValue) {
        this.ttlValue = ttlValue;
    }

    @NotNull
    public String getTtlUnit() {
        return this.ttlUnit;
    }

    public void setTtlUnit(@NotNull String ttlUnit) {
        this.ttlUnit = ttlUnit;
    }

    @DecimalMin("0.1")
    @DecimalMax("0.9")
    public double getIntervalRatio() {
        return this.intervalRatio;
    }

    public void setIntervalRatio(@DecimalMin("0.1") @DecimalMax("0.9") double intervalRatio) {
        this.intervalRatio = intervalRatio;
    }

    @Override
    public String toString() {
        return (new ToStringCreator(this)).append("enabled", this.enabled).append("ttlValue", this.ttlValue).append("ttlUnit", this.ttlUnit).append("intervalRatio", this.intervalRatio).toString();
    }
}
