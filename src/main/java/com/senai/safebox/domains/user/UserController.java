package com.senai.safebox.domains.user;

import com.senai.safebox.domains.user.dtos.UserCreateDTO;
import com.senai.safebox.domains.user.dtos.UserUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getUserAll() {

        List<UserEntity> users = userService.findAllUsers();

        return users.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable("id") Long id) {
        Optional<UserEntity> user = userService.findById(id);

        return user
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> createUser(@RequestBody UserCreateDTO userCreateDTO) {

        UserEntity newUser = userService.saveUserWithRoles(userCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {

        UserEntity user = userService.updateUser(userUpdateDTO);

        return ResponseEntity.ok(user);
    };

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {

        boolean userDeleted = userService.deleteUser(id);

        return ResponseEntity.ok().body(userDeleted);
    };



}



































