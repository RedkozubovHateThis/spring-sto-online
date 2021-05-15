package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.exception.UnauthorizedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.SubscriptionTypeRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SubscriptionTypeResourceRepository implements ResourceRepository<SubscriptionType, Long> {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Class<SubscriptionType> getResourceClass() {
        return SubscriptionType.class;
    }

    @Override
    public SubscriptionType findOne(Long aLong, QuerySpec querySpec) {
        return subscriptionTypeRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<SubscriptionType> findAll(QuerySpec querySpec) {
        return querySpec.apply( subscriptionTypeRepository.findAll() );
    }

    @Override
    public ResourceList<SubscriptionType> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( subscriptionTypeRepository.findAllById( collection ) );
    }

    @Override
    public <S extends SubscriptionType> S save(S s) {
        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isAdmin(currentUser) )
            throw new UnauthorizedException("Вам запрещено сохранять тарифы!");

        if ( s.getDurationDays() == null || s.getDurationDays() <= 0 )
            throw new BadRequestException( "Наверно указано количество дней!" );

        if ( s.getCost() == null || s.getCost() <= 0 )
            throw new BadRequestException( "Наверно указана стоимость тарифа!" );

        return subscriptionTypeRepository.save( s );
    }

    @Override
    public <S extends SubscriptionType> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
