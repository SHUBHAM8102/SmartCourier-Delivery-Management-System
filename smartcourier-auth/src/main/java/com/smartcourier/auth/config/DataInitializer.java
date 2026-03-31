package com.smartcourier.auth.config;

import com.smartcourier.auth.entity.Role;
import com.smartcourier.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("CUSTOMER", "Customer role for end users");
        createRoleIfNotExists("ADMIN", "Administrator role for system management");
    }

    private void createRoleIfNotExists(String roleCode, String description) {
        if (roleRepository.findByRoleCode(roleCode).isEmpty()) {
            Role role = Role.builder()
                    .roleCode(roleCode)
                    .description(description)
                    .build();
            roleRepository.save(role);
        }
    }
}
