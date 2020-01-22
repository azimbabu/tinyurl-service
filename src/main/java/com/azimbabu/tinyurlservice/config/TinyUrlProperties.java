package com.azimbabu.tinyurlservice.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tinyurl")
@Data
@Builder(toBuilder = true)
public class TinyUrlProperties {
  private Integer maxShortUrlRetry;
  private Integer shortUrlLength;
  private Integer defaultUrlExpirationInDays;
  private Integer maxUrlExpirationInDays;
}
