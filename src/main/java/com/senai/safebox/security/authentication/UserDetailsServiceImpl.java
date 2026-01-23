package com.senai.safebox.security.authentication;

import com.senai.safebox.domains.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/*Classe de integração entre o Spring Security e o banco de dados, o Spring vai usar essa classe sempre que precisar verificar credenciais*/
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
    };

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
            .findByUsername(username)
            .map(UserDetailsImpl::new)
            .orElseThrow(() -> new UsernameNotFoundException("User " + "\"" + username + "\"" + " not found"));
    }
}
