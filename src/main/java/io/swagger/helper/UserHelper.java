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

    public static Boolean isClient() {
        return hasRole( "CLIENT" );
    }

    public static Boolean isClient(User user) {
        return hasRole( user, "CLIENT" );
    }

    public static Boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static Boolean isAdmin(User user) {
        return hasRole( user, "ADMIN" );
    }

    public static Boolean isAdminOrModerator() {
        return hasRole("ADMIN") || hasRole("MODERATOR");
    }

    public static Boolean isAdminOrModerator(User user) {
        return hasRole( user, "ADMIN" ) || hasRole( user, "MODERATOR" );
    }

    public static Boolean isModerator() {
        return hasRole("MODERATOR");
    }

    public static Boolean isModerator(User user) {
        return hasRole( user, "MODERATOR" );
    }

    public static Boolean isServiceLeader() {
        return hasRole("SERVICE_LEADER");
    }

    public static Boolean isServiceLeader(User user) {
        return hasRole( user, "SERVICE_LEADER" );
    }

    public static Boolean isFreelancer() {
        return hasRole("FREELANCER");
    }

    public static Boolean isFreelancer(User user) {
        return hasRole( user, "FREELANCER" );
    }

    public static Boolean isServiceLeaderOrFreelancer() {
        return hasRole("FREELANCER") || hasRole("SERVICE_LEADER");
    }

    public static Boolean isServiceLeaderOrFreelancer(User user) {
        return hasRole( user, "FREELANCER" ) || hasRole( user, "SERVICE_LEADER" );
    }

}
