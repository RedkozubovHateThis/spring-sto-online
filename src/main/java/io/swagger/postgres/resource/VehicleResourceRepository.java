package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.ServiceWork;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VehicleResourceRepository implements ResourceRepository<Vehicle, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;

    @Override
    public Class<Vehicle> getResourceClass() {
        return Vehicle.class;
    }

    @Override
    public Vehicle findOne(Long aLong, QuerySpec querySpec) {
        return vehicleRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<Vehicle> findAll(QuerySpec querySpec) {
        return querySpec.apply( vehicleRepository.findAll() );
    }

    @Override
    public ResourceList<Vehicle> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( vehicleRepository.findAllById( collection ) );
    }

    @Override
    public <S extends Vehicle> S save(S s) {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isServiceLeader( currentUser ) && currentUser.getProfile() != null && s.getId() == null ) {
            s.setCreatedBy( currentUser.getProfile() );
        }

        if ( s.getVinNumber() == null || s.getVinNumber().length() == 0 )
            throw new BadRequestException("VIN-номер не может быть пустым!");
        if ( s.getModelName() == null || s.getModelName().length() == 0 )
            throw new BadRequestException("Марка/модель не может быть пустой!");
        if ( s.getRegNumber() == null || s.getRegNumber().length() == 0 )
            throw new BadRequestException("Регистрационный номер не может быть пустым!");
        if ( s.getYear() == null )
            throw new BadRequestException("Год выпуска не может быть пустым!");

        prepareVinNumber(s);
        prepareRegNumber(s);

        Boolean isVehicleExists;

        if ( s.getId() != null )
            isVehicleExists = vehicleRepository.isVehicleExistsVinNotSelf( s.getVinNumber(), s.getId() );
        else
            isVehicleExists = vehicleRepository.isVehicleExistsVin( s.getVinNumber() );

        if ( isVehicleExists )
            throw new BadRequestException("Автомобиль с таким VIN-номером уже существует!");

        return vehicleRepository.save( s );
    }

    @Override
    public <S extends Vehicle> S create(S s) {
        s.setDeleted(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять автомобили!");

        Vehicle vehicle = vehicleRepository.findById(aLong).orElse(null);

        if ( vehicle == null )
            throw new ResourceNotFoundException("Автомобиль не найден!");

        List<ServiceDocument> serviceDocuments = serviceDocumentRepository.findByVehicleIdOrderByNumber( vehicle.getId() );
        if ( serviceDocuments.size() > 0 ) {
            if ( serviceDocuments.size() > 10 )
                throw new BadRequestException( String.format( "Данный автомобиль указан в %s заказ-нарядах!", serviceDocuments.size() ) );
            else {
                List<String> documentsNumbers = serviceDocuments.stream().map( ServiceDocument::getNumber ).collect(Collectors.toList() );
                throw new BadRequestException( String.format( "Данный автомобиль указан в следующих заказ-нарядах: %s!", String.join(",", documentsNumbers) ) );
            }
        }

        vehicle.setDeleted(true);
        vehicleRepository.save(vehicle);
    }

    private void prepareVinNumber(Vehicle vehicle) {
        String vinNumber = vehicle.getVinNumber();
        vinNumber = vinNumber.trim();
        vinNumber = vinNumber.toUpperCase();
        vinNumber = vinNumber.replaceAll(" ", "");

        vehicle.setVinNumber( vinNumber );
    }

    private void prepareRegNumber(Vehicle vehicle) {
        String regNumber = vehicle.getRegNumber();
        regNumber = regNumber.trim();
        regNumber = regNumber.toUpperCase();
        regNumber = regNumber.replaceAll(" ", "");

        vehicle.setRegNumber( regNumber );
    }
}
