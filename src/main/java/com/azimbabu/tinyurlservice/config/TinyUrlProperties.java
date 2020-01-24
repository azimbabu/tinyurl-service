package com.azimbabu.tinyurlservice.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tinyurl")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TinyUrlProperties {
  private Integer maxShortUrlRetry;
  private Integer shortUrlLength;
  private Integer defaultUrlExpirationInDays;
  private Integer maxUrlExpirationInDays;
}
