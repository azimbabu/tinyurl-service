CREATE KEYSPACE tinyurl_service WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1}
                                 AND DURABLE_WRITES = true;