package com.azimbabu.tinyurlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("tiny_url")
public class TinyUrl {

  @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
  private String shortUrl;

  @Column("user")
  private UserUDT user;

  @NotEmpty(message = "Original url should not be empty")
  private String originalUrl;

  private String customAlias;

  @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
  private Date createdAt;

  private Date expiredAt;
}
