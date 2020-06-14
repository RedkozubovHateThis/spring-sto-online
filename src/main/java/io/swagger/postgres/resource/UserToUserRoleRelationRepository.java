//package io.swagger.postgres.resource;
//
//import io.crnk.core.queryspec.QuerySpec;
//import io.crnk.core.repository.ManyRelationshipRepository;
//import io.crnk.core.repository.RelationshipMatcher;
//import io.crnk.core.resource.list.DefaultResourceList;
//import io.crnk.core.resource.list.ResourceList;
//import io.swagger.postgres.model.security.User;
//import io.swagger.postgres.model.security.UserRole;
//import io.swagger.postgres.repository.UserRepository;
//import io.swagger.postgres.repository.UserRoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class UserToUserRoleRelationRepository implements ManyRelationshipRepository<User, Long, UserRole, Long> {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private UserRoleRepository userRoleRepository;
//
//    @Override
//    public RelationshipMatcher getMatcher() {
//        return new RelationshipMatcher().rule().target(UserRole.class).add();
//    }
//
//    @Override
//    public void setRelations(User user, Collection<Long> collection, String s) {
//        Set<UserRole> userRoles = new HashSet<>();
//        userRoles.addAll( userRoleRepository.findAllById( collection ) );
//        user.setRoles(userRoles);
//        userRepository.save(user);
//    }
//
//    @Override
//    public void addRelations(User user, Collection<Long> collection, String s) {
//        Set<UserRole> userRoles = user.getRoles();
//        userRoles.addAll( userRoleRepository.findAllById( collection ) );
//        user.setRoles(userRoles);
//        userRepository.save(user);
//    }
//
//    @Override
//    public void removeRelations(User user, Collection<Long> collection, String s) {
//        Set<UserRole> userRoles = user.getRoles();
//        userRoles.removeAll( userRoleRepository.findAllById( collection ) );
//        user.setRoles(userRoles);
//        userRepository.save(user);
//    }
//
//    @Override
//    public Map<Long, ResourceList<UserRole>> findManyRelations(Collection<Long> collection, String s, QuerySpec querySpec) {
//        Map<Long, ResourceList<UserRole>> map = new HashMap<>();
//        for (Long id : collection) {
//            DefaultResourceList<UserRole> list = new DefaultResourceList<>();
//            User user = userRepository.findById(id).orElse(null);
//
//            if ( user != null ) {
//                Set<UserRole> userRoles = user.getRoles();
//                list.addAll(userRoles);
//            }
//            map.put(id, list);
//        }
//        return map;
//    }
//}
