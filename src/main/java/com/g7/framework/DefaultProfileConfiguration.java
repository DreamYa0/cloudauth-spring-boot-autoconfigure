package com.g7.framework;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dreamyao
 * @title
 * @date 2019-06-06 11:17
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({CloudAuthProperties.class})
public class DefaultProfileConfiguration {

    private CloudAuthProperties cloudAuthProperties;

    public DefaultProfileConfiguration(CloudAuthProperties cloudAuthProperties) {
        this.cloudAuthProperties = cloudAuthProperties;
    }

    @Bean
    @ConditionalOnMissingBean(value = DefaultProfile.class)
    public DefaultProfile defaultProfile() {
        return DefaultProfile.getProfile(cloudAuthProperties.getRegion(), cloudAuthProperties.getAccess(), cloudAuthProperties.getSecret());
    }

    @Bean
    @ConditionalOnMissingBean(value = IAcsClient.class)
    public IAcsClient iAcsClient() {
        return new DefaultAcsClient(defaultProfile());
    }

    @Bean
    @ConditionalOnMissingBean(value = CloudAuthService.class)
    public CloudAuthService cloudAuthService() {
        return new CloudAuthService(iAcsClient(), cloudAuthProperties);
    }
}
