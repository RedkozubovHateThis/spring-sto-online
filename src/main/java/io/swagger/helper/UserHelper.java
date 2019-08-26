package io.swagger.helper;

import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

public class UserHelper {

    public static User getUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ( auth == null ) return null;

        return (User) auth.getPrincipal();

    }

    public static Boolean hasRole(String roleName) {

        User user = getUser();
        if ( user == null ) return false;

        return user.getRoles().stream().anyMatch( userRole -> { return userRole.getName().equals( roleName ); } );

    }

    public static Boolean hasRole(User user, String roleName) {

        if ( user == null ) return false;

        return user.getRoles().stream().anyMatch( userRole -> { return userRole.getName().equals( roleName ); } );

    }

}
