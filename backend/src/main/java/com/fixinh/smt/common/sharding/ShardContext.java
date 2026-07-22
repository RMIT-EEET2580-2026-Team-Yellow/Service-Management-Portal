package com.fixinh.smt.common.sharding;

/** Request-scoped holder for the shard resolved by {@link ShardKeyResolver}. */
public final class ShardContext {

    private static final ThreadLocal<ShardRegion> CURRENT = new ThreadLocal<>();

    private ShardContext() {
    }

    public static void set(ShardRegion region) {
        CURRENT.set(region);
    }

    public static ShardRegion get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
