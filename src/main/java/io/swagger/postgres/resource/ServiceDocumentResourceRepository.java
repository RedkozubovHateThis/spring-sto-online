package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceDocumentResourceRepository implements ResourceRepository<ServiceDocument, Long> {

    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;

    @Override
    public Class<ServiceDocument> getResourceClass() {
        return ServiceDocument.class;
    }

    @Override
    public ServiceDocument findOne(Long aLong, QuerySpec querySpec) {
        return serviceDocumentRepository.findById( aLong ).orElse(null);
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
        return serviceDocumentRepository.save( s );
    }

    @Override
    public <S extends ServiceDocument> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
