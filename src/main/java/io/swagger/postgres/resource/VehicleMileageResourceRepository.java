package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.VehicleMileage;
import io.swagger.postgres.repository.VehicleMileageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class VehicleMileageResourceRepository implements ResourceRepository<VehicleMileage, Long> {

    @Autowired
    private VehicleMileageRepository vehicleMileageRepository;

    @Override
    public Class<VehicleMileage> getResourceClass() {
        return VehicleMileage.class;
    }

    @Override
    public VehicleMileage findOne(Long aLong, QuerySpec querySpec) {
        return vehicleMileageRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<VehicleMileage> findAll(QuerySpec querySpec) {
        return querySpec.apply( vehicleMileageRepository.findAll() );
    }

    @Override
    public ResourceList<VehicleMileage> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( vehicleMileageRepository.findAllById( collection ) );
    }

    @Override
    public <S extends VehicleMileage> S save(S s) {
        return vehicleMileageRepository.save( s );
    }

    @Override
    public <S extends VehicleMileage> S create(S s) {
        s.setDeleted(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
