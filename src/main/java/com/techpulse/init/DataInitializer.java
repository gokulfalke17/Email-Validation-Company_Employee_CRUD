package com.techpulse.init;

import com.techpulse.entity.User;
import com.techpulse.entity.enums.Role;
import com.techpulse.repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);

            User owner = new User();
            owner.setUsername("owner");
            owner.setEmail("owner@gmail.com");
            owner.setPassword(passwordEncoder.encode("owner"));
            owner.setRole(Role.OWNER);
            owner.setActive(true);
            userRepository.save(owner);

            User user = new User();
            user.setUsername("user");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(Role.USER);
            user.setActive(true);
            userRepository.save(user);
        }
    }
}

