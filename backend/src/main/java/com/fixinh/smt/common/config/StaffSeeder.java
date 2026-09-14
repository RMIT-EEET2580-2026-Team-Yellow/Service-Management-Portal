package com.fixinh.smt.common.config;

import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.common.sharding.ShardKeyResolver;
import com.fixinh.smt.common.sharding.ShardRegion;
import com.fixinh.smt.modules.auth.entity.User;
import com.fixinh.smt.modules.auth.repository.UserRepository;
import com.fixinh.smt.modules.staff.entity.Staff;
import com.fixinh.smt.modules.staff.repository.StaffShardRepository;
import com.fixinh.smt.modules.technician.entity.Technician;
import com.fixinh.smt.modules.technician.entity.TechnicianAvailability;
import com.fixinh.smt.modules.technician.repository.TechnicianAvailabilityRepository;
import com.fixinh.smt.modules.technician.repository.TechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Sample Staff accounts for local dev/testing (both regions, RECEPTIONIST + TECHNICIAN), so a
 * fresh local Mongo container (docker-compose {@code mongo} service, empty volume) doesn't force
 * you to hand-create every account through the UI before you can test Booking/Technician flows.
 * Runs after {@link MasterAdminSeeder} — same idempotency pattern (existsByEmail early-return).
 */
@Component
@Order(3)
public class StaffSeeder implements CommandLineRunner {

    @Autowired
    private StaffShardRepository staffShardRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private TechnicianAvailabilityRepository technicianAvailabilityRepository;

    @Autowired
    private ShardKeyResolver shardKeyResolver;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String SEED_PASSWORD = "Staff@123";

    @Override
    public void run(String... args) throws Exception {
        if (staffShardRepository.existsByEmail("receptionist.hn@fixinh.vn")) {
            return;
        }

        seedStaff("Nguyen Van Le Tan", "receptionist.hn@fixinh.vn", "0901111111",
                RoleConfig.RECEPTIONIST, "Thành phố Hà Nội");
        seedStaff("Tran Van Ky Thuat", "technician.hn@fixinh.vn", "0902222222",
                RoleConfig.TECHNICIAN, "Thành phố Hà Nội");
        seedStaff("Le Thi Le Tan", "receptionist.hcm@fixinh.vn", "0903333333",
                RoleConfig.RECEPTIONIST, "Thành phố Hồ Chí Minh");
        seedStaff("Pham Van Ky Thuat", "technician.hcm@fixinh.vn", "0904444444",
                RoleConfig.TECHNICIAN, "Thành phố Hồ Chí Minh");

        System.out.println("Seeded 4 sample staff (2 per region).");
    }

    private void seedStaff(String fullName, String email, String phone, RoleConfig role, String province) {
        Staff staff = Staff.builder()
                .fullName(fullName)
                .email(email)
                .phoneNumber(phone)
                .role(role)
                .streetNumber("1")
                .streetName("Sample Street")
                .ward("Ward 1")
                .province(province)
                .active(true)
                .build();

        ShardRegion region = shardKeyResolver.resolve(province);
        Staff saved = staffShardRepository.insert(staff, region);

        User user = User.builder()
                .staffId(saved.getId())
                .email(email)
                .passwordHash(passwordEncoder.encode(SEED_PASSWORD))
                .role(role)
                .build();
        userRepository.save(user);

        if (role == RoleConfig.TECHNICIAN) {
            Technician technician = Technician.builder()
                    .staffId(saved.getId())
                    .build();
            Technician savedTechnician = technicianRepository.save(technician);

            // Without this, TechnicianServiceImpl.findAvailable() has nothing to match against
            TechnicianAvailability availability = TechnicianAvailability.builder()
                    .technicianId(savedTechnician.getId())
                    .workingDays(List.of("MON", "TUE", "WED", "THU", "FRI"))
                    .dailyStart(LocalTime.of(8, 0))
                    .dailyEnd(LocalTime.of(20, 0))
                    .currentlyAvailable(true)
                    .build();
            technicianAvailabilityRepository.save(availability);
        }
    }
}
