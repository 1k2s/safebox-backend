package com.senai.safebox.domains.user;

import com.senai.safebox.domains.user.dtos.UserCreateDTO;
import com.senai.safebox.domains.user.dtos.UserRegisterRequestDTO;
import com.senai.safebox.domains.user.dtos.UserUpdateDTO;
import com.senai.safebox.domains.user.enums.UserRoles;
import com.senai.safebox.domains.user.exceptions.UserAlreadyRegisteredException;
import com.senai.safebox.domains.user.exceptions.UserNotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Configuration
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    };

    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    };

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    };

    public UserEntity saveUserWithRoles(UserCreateDTO userCreateDTO){

        if (userRepository.existsByUsername(userCreateDTO.username())) {
            throw new UserAlreadyRegisteredException("Usuário já Cadastrado no Sistema");
        }

        String encodedPassword = passwordEncoder.encode(userCreateDTO.password());

        UserEntity newUser = new UserEntity(userCreateDTO.username(), encodedPassword, userCreateDTO.role());

        return userRepository.save(newUser);
    };

    public UserEntity saveRegularUser(UserRegisterRequestDTO userRegisterRequestDTO) {

        if (userRepository.existsByUsername(userRegisterRequestDTO.username())) {
            throw new UserAlreadyRegisteredException("Usuário já Cadastrado no Sistema");
        }

        String encodedPassword = passwordEncoder.encode(userRegisterRequestDTO.password());

        UserEntity newUser = new UserEntity(userRegisterRequestDTO.username(), encodedPassword, UserRoles.USER);

        return userRepository.save(newUser);
    };

    public UserEntity updateUser(UserUpdateDTO userUpdateDTO) {

        /*Verificando se o User existe*/
        UserEntity userEntity = userRepository.findById(userUpdateDTO.id())
            .orElseThrow(() -> new UserNotFoundException("Usuário não localizado!"));


        /*Verificando se já existe um usuário com o mesmo username*/
        userRepository.findByUsername(userUpdateDTO.username())
            .ifPresent( user -> {
                if (!user.getId().equals(userUpdateDTO.id())) {
                    throw new UserAlreadyRegisteredException("Usuário já cadastrado no sistema!");
                }
            })
        ;


        UserRoles role = UserRoles.valueOf(userUpdateDTO.role().name());

        userEntity.setUsername(userUpdateDTO.username());
        userEntity.setPassword(userUpdateDTO.password());
        userEntity.setRole(role);

        return userRepository.save(userEntity);
    };

    public boolean deleteUser(Long id) {

        return userRepository.findById(id)
            .map(user -> {
                userRepository.deleteById(user.getId());
                return true;
            })
            .orElseThrow(() -> new UserNotFoundException("Usuário não localizado!"))
        ;
    };

}



























