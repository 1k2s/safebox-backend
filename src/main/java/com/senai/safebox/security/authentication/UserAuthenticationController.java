package com.senai.safebox.security.authentication;

import com.senai.safebox.security.jwt.JwtService;
import com.senai.safebox.domains.user.UserEntity;

import com.senai.safebox.domains.user.UserService;
import com.senai.safebox.domains.user.dtos.UserLoginRequest;
import com.senai.safebox.domains.user.dtos.UserLoginResponse;
import com.senai.safebox.domains.user.dtos.UserRegisterRequestDTO;
import com.senai.safebox.domains.user.dtos.UserRegisterResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class UserAuthenticationController {


    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public UserAuthenticationController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {

        try{

            /*Criando o "token de autenticação não autenticado" com as informações recebidas*/
            /*Possui por padrão o authenticated = false*/
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginRequest.username(), userLoginRequest.password());

            /*O Authentication Manager é o metodo que valída se o authenticationToken existe na base de dados*/
            /*Ele chama o UserDetailsServiceImpl e usa o metodo loadUserByUsername*/
            /*Se for válido, retorna um Authentication autenticado(authenticated = true) */
            Authentication authentication = authenticationManager.authenticate(authenticationToken);


            /*Fazendo um downCasting para recuperar o userDetailsImpl*/
            UserDetailsImpl userDetails =  (UserDetailsImpl) authentication.getPrincipal();

            /*Recuperando a role*/
            UserEntity user = userDetails.getUser();
            String role = user.getRole().name();


            /*Passando o UserDetailsImpl com as informações do User para o JWT criar o token*/
            String token = jwtService.generateToken(userDetails);
            Long expiresIn = jwtService.getExpirationTime();

            UserLoginResponse response = new UserLoginResponse(token, userDetails.getUsername(), role, expiresIn);

            return ResponseEntity.ok().body(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais Inválidas");
        }
    };

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {

        UserEntity newUser = userService.saveRegularUser(userRegisterRequestDTO);

        UserRegisterResponseDTO responseDTO = new UserRegisterResponseDTO(newUser.getId(), newUser.getUsername(), "Usuário Criado com sucesso!");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    };

}
