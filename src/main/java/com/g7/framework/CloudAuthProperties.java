package com.g7.framework;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dreamyao
 * @title
 * @date 2019-06-09 22:47
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "cloud.auth")
public class CloudAuthProperties {

    /**
     * 地区ID
     */
    private String region = "cn-hangzhou";

    /**
     * access key
     */
    private String access;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 业务类型
     */
    private String biz = "driver-mini-h5";



    /**
     *
     * APP业务类型
     */
    private String appBizType = "driver-min-app";

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public String getAppBizType() {
        return appBizType;
    }

    public void setAppBizType(String appBizType) {
        this.appBizType = appBizType;
    }
}
