//package io.swagger.postgres.resource;
//
//import io.crnk.core.queryspec.QuerySpec;
//import io.crnk.core.repository.ManyRelationshipRepository;
//import io.crnk.core.repository.OneRelationshipRepository;
//import io.crnk.core.repository.RelationshipMatcher;
//import io.crnk.core.resource.list.DefaultResourceList;
//import io.swagger.postgres.model.payment.Subscription;
//import io.swagger.postgres.model.security.User;
//import io.swagger.postgres.repository.UserRepository;
//import io.swagger.postgres.repository.SubscriptionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class UserToSubscriptionRelationRepository implements OneRelationshipRepository<User, Long, Subscription, Long> {
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
//    public void setRelation(User user, Long aLong, String s) {
//        Subscription subscription = subscriptionRepository.findById( aLong ).orElse(null);
//        user.setCurrentSubscription( subscription );
//    }
//
//    @Override
//    public Map<Long, Subscription> findOneRelations(Collection<Long> collection, String s, QuerySpec querySpec) {
//        Map<Long, Subscription> map = new HashMap<>();
//        for (Long id : collection) {
//            User user = userRepository.findById(id).orElse(null);
//
//            if ( user != null ) {
//                Subscription currentSubscription = user.getCurrentSubscription();
//                if (currentSubscription != null)
//                    map.put(id, currentSubscription);
//            }
//        }
//        return map;
//    }
//}
