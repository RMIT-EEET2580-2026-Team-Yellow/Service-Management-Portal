package com.fixinh.smt.common.config;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * Provides the two physical Staff databases required by 3.1.U1 (Northern/Southern region shard).
 * {@link com.fixinh.smt.common.sharding.MongoShardRouter} picks between these two beans; no other
 * module should inject {@link MongoTemplate} directly when working with sharded Staff data.
 *
 * <p>Also declares the default unqualified {@code mongoTemplate} bean explicitly: once any
 * custom-named {@link MongoTemplate} bean exists, Spring Boot's autoconfiguration backs off from
 * creating its own default one, so every other module (Booking, Notification, Statistics, Auth,
 * Province, ...) would fail to autowire {@link MongoTemplate} without this.
 */
@Configuration
public class MongoShardConfig {

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }

    @Bean
    @Qualifier("northernTemplate")
    public MongoTemplate northernTemplate(@Value("${smt.mongo.northern-uri}") String uri) {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(MongoClients.create(uri), "smt_northern");
        return new MongoTemplate(factory);
    }

    @Bean
    @Qualifier("southernTemplate")
    public MongoTemplate southernTemplate(@Value("${smt.mongo.southern-uri}") String uri) {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(MongoClients.create(uri), "smt_southern");
        return new MongoTemplate(factory);
    }
}
