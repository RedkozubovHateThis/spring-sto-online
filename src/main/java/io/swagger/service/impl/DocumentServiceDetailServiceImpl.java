package io.swagger.service.impl;

import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.SubscriptionRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.DocumentServiceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class DocumentServiceDetailServiceImpl implements DocumentServiceDetailService {

    @Autowired
    private DocumentServiceDetailRepository documentsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public List<Integer> collectPaidDocumentIds(Date startDate, Date endDate) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return null;

        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) || currentUser.getIsAccessRestricted() ) return null;

        if ( UserHelper.isServiceLeader( currentUser ) && !currentUser.isServiceLeaderValid() ) return null;
        if ( UserHelper.isFreelancer( currentUser ) && !currentUser.isFreelancerValid() ) return null;

        List<Subscription> subscriptions;
        List<Integer> paidDocumentIds = new ArrayList<>();

        if ( startDate != null && endDate != null ) {
            subscriptions = subscriptionRepository.findAllByUserIdAndBetweenDates( currentUser.getId(), startDate, endDate );
        }
        else if ( startDate != null ) {
            subscriptions = subscriptionRepository.findAllByUserIdAndStartDate( currentUser.getId(), startDate );
        }
        else if ( endDate != null ) {
            subscriptions = subscriptionRepository.findAllByUserIdAndEndDate( currentUser.getId(), endDate );
        }
        else {
            subscriptions = subscriptionRepository.findAllByUserId( currentUser.getId() );
        }

        if ( UserHelper.isServiceLeader( currentUser ) ) {

            for (Subscription subscription : subscriptions) {

                paidDocumentIds.addAll( documentsRepository.collectPaidDocumentsByOrganizationIdAndDates(
                        subscription.getDocumentsCount(), currentUser.getOrganizationId(),
                        subscription.getStartDate(), subscription.getEndDate()
                ) );

            }

            Date firstSubscriptionDate = getFirstSubscriptionDate(currentUser);

            paidDocumentIds.addAll( documentsRepository.collectPaidDocumentsByOrganizationIdAndBefore(
                    currentUser.getOrganizationId(),
                    firstSubscriptionDate
            ) );

        }
        else if ( UserHelper.isFreelancer( currentUser ) ) {

            for (Subscription subscription : subscriptions) {

                paidDocumentIds.addAll( documentsRepository.collectPaidDocumentsByOrganizationIdAndDatesAndManagerId(
                        subscription.getDocumentsCount(), currentUser.getOrganizationId(),
                        subscription.getStartDate(), subscription.getEndDate(), currentUser.getManagerId()
                ) );

            }

            Date firstSubscriptionDate = getFirstSubscriptionDate(currentUser);

            paidDocumentIds.addAll( documentsRepository.collectPaidDocumentsByOrganizationIdAndBeforeAndManagerId(
                    currentUser.getOrganizationId(),
                    firstSubscriptionDate,
                    currentUser.getManagerId()
            ) );

        }


        return paidDocumentIds;
    }

    @Override
    public Date getFirstSubscriptionDate(User user) {
        if ( user == null ) return null;

        return subscriptionRepository.findFirstSubscriptionDateByUserId( user.getId() );
    }

}
