package com.fixinh.smt.common.sharding;

import org.springframework.stereotype.Component;

/**
 * Resolves a Vietnamese province name to {@link ShardRegion} using the new (2025) provincial
 * boundary: Hue and everything south of it -> SOUTHERN; Quang Tri and everything north -> NORTHERN
 * (3.1.U1). Used automatically on Staff create/update/read (3.1.U2) so callers never pick the
 * shard themselves.
 */
@Component
public class ShardKeyResolver {

    public ShardRegion resolve(String province) {
        // TODO Milestone 2: look up the ordered province list and split at Hue/Quang Tri.
        throw new UnsupportedOperationException("Milestone 2");
    }
}
