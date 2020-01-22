package com.azimbabu.tinyurlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@UserDefinedType("user_udt")
public class UserUDT {
  @NotEmpty(message = "User name should not be empty")
  private String username;

  @NotEmpty(message = "User email should not be empty")
  private String email;

  private Date lastLoginDate;
}
