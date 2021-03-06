package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.Customer;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.CustomerRepository;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerResourceRepository implements ResourceRepository<Customer, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;

    @Override
    public Class<Customer> getResourceClass() {
        return Customer.class;
    }

    @Override
    public Customer findOne(Long aLong, QuerySpec querySpec) {
        return customerRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<Customer> findAll(QuerySpec querySpec) {
        return querySpec.apply( customerRepository.findAll() );
    }

    @Override
    public ResourceList<Customer> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( customerRepository.findAllById( collection ) );
    }

    @Override
    public <S extends Customer> S save(S s) {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null && s.getId() == null ) {
            s.setCreatedBy( currentUser.getProfile() );
        }

        if ( s.getInn() == null || s.getInn().isEmpty() )
            throw new BadRequestException("ИНН не может быть пустым!");
//        if ( s.getPhone() == null || s.getPhone().isEmpty() )
//            throw new BadRequestException("Телефон не может быть пустым!");
        if ( s.getPhone() != null && !s.getPhone().isEmpty() && !userService.isPhoneValid( s.getPhone() ) )
            throw new BadRequestException("Неверный номер телефона!");
        if ( s.getEmail() != null && s.getEmail().length() == 0 )
            s.setEmail(null);
        if ( s.getPhone() != null && s.getPhone().length() == 0 )
            s.setPhone(null);
        else if ( s.getPhone() != null && s.getPhone().length() > 0 )
            userService.processPhone(s);

//        Boolean isExistsByPhone;
//
//        if ( s.getId() != null )
//            isExistsByPhone = customerRepository.isCustomerExistsPhoneNotSelf( s.getPhone(), s.getId() );
//        else
//            isExistsByPhone = customerRepository.isCustomerExistsPhone( s.getPhone() );
//
//        if ( isExistsByPhone )
//            throw new BadRequestException("Данный телефон уже указан у другого заказчика!");
//
//        if ( s.getEmail() != null ) {
//
//            Boolean isExistsByEmail;
//
//            if ( s.getId() != null )
//                isExistsByEmail = customerRepository.isCustomerExistsEmailNotSelf( s.getEmail(), s.getId() );
//            else
//                isExistsByEmail = customerRepository.isCustomerExistsEmail( s.getEmail() );
//
//            if ( isExistsByEmail )
//                throw new BadRequestException("Данная почта уже указана у другого заказчика!");
//
//        }

        Boolean isExistsByInn;

        if ( s.getId() != null )
            isExistsByInn = customerRepository.isCustomerExistsInnNotSelf( s.getInn(), s.getId() );
        else
            isExistsByInn = customerRepository.isCustomerExistsInn( s.getInn() );

        if ( isExistsByInn )
            throw new BadRequestException("Данный ИНН уже указан у другого заказчика!");

        return customerRepository.save(s);
    }

    @Override
    public <S extends Customer> S create(S s) {
        s.setDeleted(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять заказчиков!");

        Customer customer = customerRepository.findById(aLong).orElse(null);

        if ( customer == null )
            throw new ResourceNotFoundException("Заказчик не найден!");

        List<ServiceDocument> serviceDocuments = serviceDocumentRepository.findByCustomerIdOrderByNumber( customer.getId() );
        if ( serviceDocuments.size() > 0 ) {
            if ( serviceDocuments.size() > 10 )
                throw new BadRequestException( String.format( "Данный заказчик указан в %s заказ-нарядах!", serviceDocuments.size() ) );
            else {
                List<String> documentsNumbers = serviceDocuments.stream().map( ServiceDocument::getNumber ).collect(Collectors.toList() );
                throw new BadRequestException( String.format( "Данный заказчик указан в следующих заказ-нарядах: %s!", String.join(",", documentsNumbers) ) );
            }
        }

        customer.setDeleted(true);
        customerRepository.save(customer);
    }
}
