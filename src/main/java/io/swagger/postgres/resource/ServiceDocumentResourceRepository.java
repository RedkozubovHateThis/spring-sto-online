package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.MetaRepository;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.core.resource.meta.MetaInformation;
import io.swagger.controller.WebSocketController;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.enums.ServiceDocumentPaidStatus;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.JsonApiListMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceDocumentResourceRepository implements ResourceRepository<ServiceDocument, Long>, MetaRepository<ServiceDocument> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;

    @Autowired
    private WebSocketController webSocketController;

    @Override
    public Class<ServiceDocument> getResourceClass() {
        return ServiceDocument.class;
    }

    @Override
    public ServiceDocument findOne(Long aLong, QuerySpec querySpec) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin(currentUser) && currentUser.getProfile() == null )
            throw new ResourceNotFoundException("Заказ-наряд не найден!");

        ServiceDocument serviceDocument = serviceDocumentRepository.findById( aLong ).orElse(null);

        if ( serviceDocument == null )
            throw new ResourceNotFoundException("Заказ-наряд не найден!");

        Profile executor = serviceDocument.getExecutor();
        Profile client = serviceDocument.getClient();

        if ( UserHelper.isClient( currentUser ) && ( client == null || !client.getId().equals( currentUser.getProfile().getId() ) ) )
            throw new ForbiddenException("Заказ-наряд не найден!");
        if ( UserHelper.isServiceLeader( currentUser ) && ( executor == null || !executor.getId().equals( currentUser.getProfile().getId() ) ) )
            throw new ForbiddenException("Заказ-наряд не найден!");

        return serviceDocument;
    }

    @Override
    public ResourceList<ServiceDocument> findAll(QuerySpec querySpec) {
        return querySpec.apply( serviceDocumentRepository.findAll() );
    }

    @Override
    public ResourceList<ServiceDocument> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( serviceDocumentRepository.findAllById( collection ) );
    }

    @Override
    public <S extends ServiceDocument> S save(S s) {
        if ( s.getId() != null && ( s.getNumber() == null || s.getNumber().length() == 0 ) )
            throw new BadRequestException("Номер заказ-наряда не может быть пустым!");
        if ( s.getStartDate() == null )
            throw new BadRequestException("Дата начала ремонта не может быть пустой!");
        if ( s.getStatus() == null )
            throw new BadRequestException("Статус заказ-наряда не может быть пустым!");
        if ( s.getExecutor() == null )
            throw new BadRequestException("Не указан исполнитель!");
        if ( s.getClient() == null )
            throw new BadRequestException("Не указан клиент!");
        if ( s.getVehicle() == null )
            throw new BadRequestException("Не указан автомобиль!");
        if ( s.getVehicleMileage() == null )
            throw new BadRequestException("Не указан пробег!");

//        Boolean isServiceDocumentExists;
//
//        if ( s.getId() != null )
//            isServiceDocumentExists = serviceDocumentRepository.isServiceDocumentExistsNumberNotSelf( s.getNumber(), s.getId() );
//        else
//            isServiceDocumentExists = serviceDocumentRepository.isServiceDocumentExistsNumber( s.getNumber() );
//
//        if ( isServiceDocumentExists )
//            throw new BadRequestException("Заказ-наряд с таким номером уже существует!");

        if ( s.getId() == null && ( s.getNumber() == null || s.getNumber().length() == 0 ) )
            generateNumber(s);

        serviceDocumentRepository.save( s );
        sendCounterRefreshMessage( s );

        return s;
    }

    @Override
    public <S extends ServiceDocument> S create(S s) {
        s.setDeleted(false);
        if ( s.getPaidStatus() == null )
            s.setPaidStatus( ServiceDocumentPaidStatus.NOT_PAID );
        if ( s.getStatus() == null )
            s.setStatus( ServiceDocumentStatus.CREATED );
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isServiceLeader( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять заказ-наряды!");

        ServiceDocument serviceDocument = serviceDocumentRepository.findById(aLong).orElse(null);

        if ( serviceDocument == null )
            throw new ResourceNotFoundException("Заказ-наряд не найден!");

        if ( serviceDocument.getStatus().equals( ServiceDocumentStatus.COMPLETED ) && UserHelper.isServiceLeader( currentUser ) )
            throw new BadRequestException("Нельзя удалять завершенные заказ-наряды!");

        serviceDocument.setDeleted( true );
        serviceDocumentRepository.save( serviceDocument );
        sendCounterRefreshMessage( serviceDocument );
    }

    @Override
    public MetaInformation getMetaInformation(Collection<ServiceDocument> resources, QuerySpec querySpec, MetaInformation current) {
        return new JsonApiListMeta(serviceDocumentRepository.count());
    }

    private void generateNumber(ServiceDocument serviceDocument) throws BadRequestException {
        Profile executor = serviceDocument.getExecutor();
        User user = executor.getUser();
        if ( user == null )
            throw new BadRequestException("Не найден исполнитель! Возможно, пользователь был удален!");

        String shortFio = user.getShortFio();
        if ( shortFio == null )
            throw new BadRequestException("Необходимо заполнить профиль исполнителя!");
        long count = serviceDocumentRepository.countAllByExecutorId(executor.getId());
        String documentNumber = String.format("%s%s", shortFio, count + 1);
        serviceDocument.setNumber( documentNumber );
    }

    private void sendCounterRefreshMessage(ServiceDocument serviceDocument) {
        try {
            Profile executor = serviceDocument.getExecutor();
            Profile client = serviceDocument.getClient();

            if ( executor != null && executor.getUser() != null )
                webSocketController.sendCounterRefreshMessage( executor.getUser() );
            if ( client != null && client.getUser() != null )
                webSocketController.sendCounterRefreshMessage( client.getUser() );
            webSocketController.sendCounterRefreshMessageToAdmins();
        }
        catch(Exception ignored) {}
    }
}
