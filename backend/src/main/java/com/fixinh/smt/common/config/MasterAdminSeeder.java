package com.fixinh.smt.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.common.sharding.ShardKeyResolver;
import com.fixinh.smt.common.sharding.ShardRegion;
import com.fixinh.smt.modules.staff.entity.Staff;
import com.fixinh.smt.modules.auth.entity.User;
import com.fixinh.smt.modules.auth.repository.UserRepository;
import com.fixinh.smt.modules.staff.repository.StaffShardRepository;

@Component
@Order(2)
public class MasterAdminSeeder implements CommandLineRunner {

    @Autowired
    private StaffShardRepository staffShardRepository;

    @Autowired
    private ShardKeyResolver shardKeyResolver;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@fixinh.vn";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String ADMIN_PROVINCE = "Thành phố Hồ Chí Minh";

    @Override
    public void run(String... args) throws Exception {
        if (staffShardRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        Staff admin = Staff.builder()
                .fullName("Master Admin")
                .email(ADMIN_EMAIL)
                .phoneNumber("0900000000")
                .role(RoleConfig.ADMIN)
                .streetNumber("1")
                .streetName("Admin Street")
                .ward("Ward 1")
                .province(ADMIN_PROVINCE)
                .active(true)
                .build();

        ShardRegion region = shardKeyResolver.resolve(ADMIN_PROVINCE);
        Staff savedAdmin = staffShardRepository.insert(admin, region);

        User user = User.builder()
                .staffId(savedAdmin.getId())
                .email(ADMIN_EMAIL)
                .passwordHash(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(RoleConfig.ADMIN)
                .build();
        userRepository.save(user);

        System.out.println("Seeded Master Admin: " + ADMIN_EMAIL + " (shard: " + region + ")");
    }
}
