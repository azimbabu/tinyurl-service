package com.azimbabu.tinyurlservice.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class TinyUrlRequest {
  private final String originalUrl;
  private final String customAlias;
  private final String username;
  private final Long expirationInSeconds;
}
