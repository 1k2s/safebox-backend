package com.senai.safebox.config.initializers;

import com.senai.safebox.domains.user.UserEntity;
import com.senai.safebox.domains.user.UserRepository;
import com.senai.safebox.domains.user.enums.UserRoles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // injeção de dependência via constructor
    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Verifica se já existe algum usuário admin
        if (userRepository.existsByRole(UserRoles.ADMIN)) {
            return;
        }

        // CriaNDO o primeiro admin
        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("user"));  // Senha temporária
        admin.setRole(UserRoles.ADMIN);

        /*Salvando no banco*/
        userRepository.save(admin);

    }
}
