package com.fixinh.smt.modules.staff.repository;

import com.fixinh.smt.common.sharding.MongoShardRouter;
import com.fixinh.smt.common.sharding.ShardRegion;
import com.fixinh.smt.modules.staff.entity.Staff;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class StaffShardRepository {

    private final MongoShardRouter router;

    public StaffShardRepository(MongoShardRouter router) {
        this.router = router;
    }

    public Staff insert(Staff staff, ShardRegion region) {
        return router.templateFor(region).insert(staff);
    }

    public Staff save(Staff staff, ShardRegion region) {
        return router.templateFor(region).save(staff);
    }

    public void delete(Staff staff, ShardRegion region) {
        router.templateFor(region).remove(staff);
    }

    public Optional<Map.Entry<ShardRegion, Staff>> findByIdWithRegion(String id) {
        for (ShardRegion region : ShardRegion.values()) {
            Staff found = router.templateFor(region).findById(id, Staff.class);
            if (found != null) {
                return Optional.of(new AbstractMap.SimpleEntry<>(region, found));
            }
        }
        return Optional.empty();
    }

    public boolean existsById(String id) {
        return findByIdWithRegion(id).isPresent();
    }

    /** Every Staff document across both shards, merged - used by the Admin/SBT Staff list (3.1). */
    public List<Staff> findAllAcrossShards() {
        List<Staff> all = new ArrayList<>();
        for (ShardRegion region : ShardRegion.values()) {
            all.addAll(router.templateFor(region).findAll(Staff.class));
        }
        return all;
    }

    public boolean existsByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        for (ShardRegion region : ShardRegion.values()) {
            if (router.templateFor(region).exists(query, Staff.class)) {
                return true;
            }
        }
        return false;
    }
}
