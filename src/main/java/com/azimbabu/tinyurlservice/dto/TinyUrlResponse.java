package com.azimbabu.tinyurlservice.dto;

import com.azimbabu.tinyurlservice.model.TinyUrl;
import com.azimbabu.tinyurlservice.service.ErrorCode;
import lombok.*;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class TinyUrlResponse {
  private final boolean success;
  private final ErrorCode errorCode;
  private final TinyUrl data;
}
