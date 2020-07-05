package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class VehicleResourceRepository implements ResourceRepository<Vehicle, Long> {

    @Autowired
    private VehicleRepository vehicleRepository;

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
        if ( s.getVinNumber() == null || s.getVinNumber().length() == 0 )
            throw new BadRequestException("VIN-номер не может быть пустым!");
        if ( s.getModelName() == null || s.getModelName().length() == 0 )
            throw new BadRequestException("Марка/модель не может быть пустой!");
        if ( s.getRegNumber() == null || s.getRegNumber().length() == 0 )
            throw new BadRequestException("Регистрационный номер не может быть пустым!");
        if ( s.getYear() == null )
            throw new BadRequestException("Год выпуска не может быть пустым!");

        prepareVinNumber(s);

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
    }

    private void prepareVinNumber(Vehicle vehicle) {
        String vinNumber = vehicle.getVinNumber();
        vinNumber = vinNumber.trim();
        vinNumber = vinNumber.toUpperCase();
        vinNumber = vinNumber.replaceAll(" ", "");

        vehicle.setVinNumber( vinNumber );
    }
}
