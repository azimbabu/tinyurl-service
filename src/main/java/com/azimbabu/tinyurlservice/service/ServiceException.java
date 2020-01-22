package com.azimbabu.tinyurlservice.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceException extends RuntimeException {

  private ErrorCode errorCode;

  public ServiceException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public ServiceException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public ServiceException(String message, Throwable cause, ErrorCode errorCode) {
    super(message, cause);
    this.errorCode = errorCode;
  }
}
