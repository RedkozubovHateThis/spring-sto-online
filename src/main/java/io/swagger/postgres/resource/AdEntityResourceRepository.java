package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.AdEntity;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.AdEntityRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

@Component
public class AdEntityResourceRepository implements ResourceRepository<AdEntity, Long> {

    @Autowired
    private AdEntityRepository adEntityRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Class<AdEntity> getResourceClass() {
        return AdEntity.class;
    }

    @Override
    public AdEntity findOne(Long aLong, QuerySpec querySpec) {
        return adEntityRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<AdEntity> findAll(QuerySpec querySpec) {
        return querySpec.apply( adEntityRepository.findAll() );
    }

    @Override
    public ResourceList<AdEntity> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( adEntityRepository.findAllById( collection ) );
    }

    @Override
    public <S extends AdEntity> S save(S s) {
        if ( s.getName() == null || s.getName().length() == 0 )
            throw new BadRequestException("Полное наименование не может быть пустым!");
        if ( s.getPhone() == null || s.getPhone().isEmpty() )
            throw new BadRequestException("Телефон не может быть пустым!");
        if ( !userService.isPhoneValid( s.getPhone() ) )
            throw new BadRequestException("Неверный номер телефона!");
        if ( s.getDescription() == null || s.getDescription().isEmpty() )
            throw new BadRequestException("Описание не может быть пустым!");

        return adEntityRepository.save( s );
    }

    @Override
    public <S extends AdEntity> S create(S s) {
        s.setDeleted(false);
        s.setCreateDate( new Date() );
        s.setActive(true);
        s.setCurrent(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять рекламные объявления!");

        AdEntity adEntity = adEntityRepository.findById(aLong).orElse(null);

        if ( adEntity == null )
            throw new ResourceNotFoundException("Рекламное объявление не найдено!");

        if ( !adEntity.getSideOffer() )
            throw new BadRequestException("Запрещено удалять не сторонние рекламные объявления!");

        adEntity.setDeleted( true );
        adEntityRepository.save( adEntity );
    }
}
