package com.azimbabu.tinyurlservice.repository;

import info.archinnov.achilles.internals.runtime.AbstractManagerFactory;
import info.archinnov.achilles.junit.AchillesTestResource;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.azimbabu.tinyurlservice.config.CassandraTestUtils.embeddedCassandra;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class TinyUrlRepositoryTests {

  @Autowired
  private TinyUrlRepository tinyUrlRepository;

  @Rule
  public AchillesTestResource<AbstractManagerFactory> achillesTestResource = embeddedCassandra();

  @Test
  void testGetById() {
    assertFalse(tinyUrlRepository.findByShortUrl("abc123").isPresent());
  }
}
