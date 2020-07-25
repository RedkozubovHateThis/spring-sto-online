package io.swagger.controller;

import io.swagger.helper.CustomerSpecificationBuilder;
import io.swagger.helper.ServiceAddonDictionarySpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.Customer;
import io.swagger.postgres.model.ServiceAddonDictionary;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.CustomerRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.CustomerResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/external/customers")
public class CustomerController {

    private final static Logger logger = LoggerFactory.getLogger( CustomerController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerResourceProcessor customerResourceProcessor;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<Customer> specification =
                CustomerSpecificationBuilder.buildSpecification( filterPayload );

        return ResponseEntity.ok(
                customerResourceProcessor.toResourcePage(
                        customerRepository.findAll(specification, pageable), params.getInclude(),
                        customerRepository.count(specification), pageable
                )
        );
    }

    @GetMapping("/list")
    public ResponseEntity findAllClients() throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isServiceLeader( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(404).build();

        List<Customer> customers;

        if ( UserHelper.isAdmin( currentUser ) )
            customers = customerRepository.findAll();
        else {
            if ( currentUser.getProfile() == null )
                return ResponseEntity.status(404).build();
            customers = customerRepository.findCustomersByExecutorId( currentUser.getProfile().getId() );
        }

        return ResponseEntity.ok(
                customerResourceProcessor.toResourceList(
                        customers,
                        null,
                        (long) customers.size(),
                        null
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity findCustomers(@RequestParam("search") String search) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(404).build();

        if ( search == null || search.length() < 3 )
            return ResponseEntity.status(400).build();

        List<Customer> customers = customerRepository.findAllByPhoneOrEmail(
                String.format("%%%s%%", search),
                String.format("%%%s%%", search),
                String.format("%%%s%%", search),
                String.format("%%%s%%", search)
        );
        if ( customers.size() == 0 )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok(
                customerResourceProcessor.toResourceList(
                        customers,
                        null,
                        (long) customers.size(),
                        null
                )
        );
    }

    @Data
    public static class FilterPayload {
        private String phone;
        private String email;
        private String fio;
        private String inn;
    }

    @Data
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("phone") && getFilter().get("phone").size() > 0 )
                filterPayload.setPhone( getFilter().get("phone").get(0) );
            if ( getFilter().containsKey("email") && getFilter().get("email").size() > 0 )
                filterPayload.setEmail( getFilter().get("email").get(0) );
            if ( getFilter().containsKey("fio") && getFilter().get("fio").size() > 0 )
                filterPayload.setFio( getFilter().get("fio").get(0) );
            if ( getFilter().containsKey("inn") && getFilter().get("inn").size() > 0 )
                filterPayload.setInn( getFilter().get("inn").get(0) );

            return filterPayload;
        }
    }
}
