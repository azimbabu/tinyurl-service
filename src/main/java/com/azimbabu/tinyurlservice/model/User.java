package com.azimbabu.tinyurlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("user")
public class User {

  @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
  private String username;

  @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
  private Date createdAt;

  @NotEmpty(message = "User email should not be empty")
  private String email;

  private Date lastLoginDate;
}
