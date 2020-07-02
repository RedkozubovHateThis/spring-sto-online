package io.swagger.postgres.resource;

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
}
