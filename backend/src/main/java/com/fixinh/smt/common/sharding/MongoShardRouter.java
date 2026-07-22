package com.fixinh.smt.common.sharding;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Holds one {@link MongoTemplate} per {@link ShardRegion} (two physical Staff databases) and
 * routes to the correct one based on {@link ShardContext} — SBP must NOT be able to specify the
 * shard directly (4.U1), it only calls the Staff module's External Interface.
 */
@Component
public class MongoShardRouter {

    private final MongoTemplate northernTemplate;
    private final MongoTemplate southernTemplate;

    public MongoShardRouter(@Qualifier("northernTemplate") MongoTemplate northernTemplate,
                             @Qualifier("southernTemplate") MongoTemplate southernTemplate) {
        this.northernTemplate = northernTemplate;
        this.southernTemplate = southernTemplate;
    }

    public MongoTemplate templateFor(ShardRegion region) {
        return region == ShardRegion.NORTHERN ? northernTemplate : southernTemplate;
    }
}
