//package io.swagger.postgres.resource;
//
//import io.crnk.core.queryspec.QuerySpec;
//import io.crnk.core.repository.ManyRelationshipRepository;
//import io.crnk.core.repository.OneRelationshipRepository;
//import io.crnk.core.repository.RelationshipMatcher;
//import io.crnk.core.resource.list.DefaultResourceList;
//import io.crnk.core.resource.list.ResourceList;
//import io.swagger.postgres.model.payment.Subscription;
//import io.swagger.postgres.model.security.User;
//import io.swagger.postgres.model.security.UserRole;
//import io.swagger.postgres.repository.SubscriptionRepository;
//import io.swagger.postgres.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class UserToSubscriptionsRelationRepository implements ManyRelationshipRepository<User, Long, Subscription, Long> {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private SubscriptionRepository subscriptionRepository;
//
//    @Override
//    public RelationshipMatcher getMatcher() {
//        return new RelationshipMatcher().rule().target(Subscription.class).add();
//    }
//
//    @Override
//    public void setRelations(User source, Collection<Long> targetIds, String fieldName) {
//        Set<Subscription> subscriptions = new HashSet<>();
//        subscriptions.addAll( subscriptionRepository.findAllById( targetIds ) );
//        source.setAllSubscriptions(subscriptions);
//        userRepository.save(source);
//    }
//
//    @Override
//    public void addRelations(User source, Collection<Long> targetIds, String fieldName) {
//        Set<Subscription> subscriptions = source.getAllSubscriptions();
//        subscriptions.addAll( subscriptionRepository.findAllById( targetIds ) );
//        source.setAllSubscriptions(subscriptions);
//        userRepository.save(source);
//    }
//
//    @Override
//    public void removeRelations(User source, Collection<Long> targetIds, String fieldName) {
//        Set<Subscription> subscriptions = source.getAllSubscriptions();
//        subscriptions.removeAll( subscriptionRepository.findAllById( targetIds ) );
//        source.setAllSubscriptions(subscriptions);
//        userRepository.save(source);
//    }
//
//    @Override
//    public Map<Long, ResourceList<Subscription>> findManyRelations(Collection<Long> sourceIds, String fieldName, QuerySpec querySpec) {
//        Map<Long, ResourceList<Subscription>> map = new HashMap<>();
//        for (Long id : sourceIds) {
//            DefaultResourceList<Subscription> list = new DefaultResourceList<>();
//            User user = userRepository.findById(id).orElse(null);
//
//            if ( user != null ) {
//                Set<Subscription> subscriptions = user.getAllSubscriptions();
//                list.addAll(subscriptions);
//            }
//            map.put(id, list);
//        }
//        return map;
//    }
//}
