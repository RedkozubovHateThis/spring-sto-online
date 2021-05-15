package io.swagger.service;

import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if ( user != null ) {

            Set<SimpleGrantedAuthority> authorities = user.getRoles().stream().map( role -> new SimpleGrantedAuthority( role.getName() ) ).collect( Collectors.toSet() );
            user.setAuthorities(authorities);

            return user;

        }

        throw new UsernameNotFoundException(username);
    }
}
