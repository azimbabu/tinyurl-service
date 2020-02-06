package com.azimbabu.tinyurlservice.config;

import info.archinnov.achilles.internals.runtime.AbstractManagerFactory;
import info.archinnov.achilles.junit.AchillesTestResource;
import info.archinnov.achilles.junit.AchillesTestResourceBuilder;

public final class CassandraTestUtils {

    public static AchillesTestResource<AbstractManagerFactory> embeddedCassandra() {
        return AchillesTestResourceBuilder
                .forJunit()
                .withScript("schema.cql")
                .createAndUseKeyspace("tinyurl_service")
                .build((cluster, statementsCache) -> null);
    }
}
