package com.azimbabu.tinyurlservice;

import info.archinnov.achilles.internals.runtime.AbstractManagerFactory;
import info.archinnov.achilles.junit.AchillesTestResource;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.azimbabu.tinyurlservice.config.CassandraTestUtils.embeddedCassandra;

@SpringBootTest
class TinyUrlServiceApplicationTests {

  @Rule
  public AchillesTestResource<AbstractManagerFactory> achillesTestResource = embeddedCassandra();

  @Test
  void contextLoads() {}
}
