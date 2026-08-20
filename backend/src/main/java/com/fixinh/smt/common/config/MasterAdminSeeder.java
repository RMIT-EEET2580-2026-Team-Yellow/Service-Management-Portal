package com.fixinh.smt.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.modules.staff.entity.Staff;
import com.fixinh.smt.modules.auth.entity.User;
import com.fixinh.smt.modules.auth.repository.UserRepository;
import com.fixinh.smt.modules.staff.repository.StaffRepository;

@Component
public class MasterAdminSeeder implements CommandLineRunner {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@fixinh.vn";
    private static final String ADMIN_PASSWORD = "Admin@123";

    @Override
    public void run(String... args) throws Exception {
        if (staffRepository.existsByEmail(ADMIN_EMAIL)) {
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
                .province("Ho Chi Minh City")
                .active(true)
                .build();
        Staff savedAdmin = staffRepository.save(admin);

        User user = User.builder()
                .staffId(savedAdmin.getId())
                .email(ADMIN_EMAIL)
                .passwordHash(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(RoleConfig.ADMIN)
                .build();
        userRepository.save(user);

        System.out.println("Seeded Master Admin: " + ADMIN_EMAIL);

        
    }
}
