package com.yrnet.spark.digitalaccount.api.functional.customer.v2;

import com.yrnet.spark.digitalaccount.api.functional.customer.v2.domain.PaginatedQuery;
import com.yrnet.spark.digitalaccount.api.functional.customer.guard.CustomerGuard;
import com.yrnet.spark.digitalaccount.api.functional.customer.v1.CustomerFacade;
import com.yrnet.spark.digitalaccount.api.technical.utils.ResponseUtils;
import com.yrnet.spark.digitalaccount.core.service.customer.CustomerOrderHistoryService;
import com.yrnet.spark.digitalaccount.core.service.customer.RGPDCustomerService;
import com.yrnet.spark.digitalaccount.core.service.social.SocialNetworkUserIdentityService;
import com.yrnet.spark.digitalaccount.dto.CustomerDTO;
import com.yrnet.spark.digitalaccount.dto.CustomerDataDTO;
import com.yrnet.spark.digitalaccount.dto.SearchResultCustomerDTO;
import com.yrnet.spark.digitalaccount.dto.SocialNetworkUserAssociations;
import com.yrnet.spark.digitalaccount.persistence.model.CustomerOrderHistory;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public/v2/customers")
public class CustomersResource {

    private final CustomerFacade customerFacade;
    private final RGPDCustomerService rgpdCustomerService;
    private final CustomerGuard customerGuard;
    private final CustomerOrderHistoryService customerOrderHistoryService;
    private final SocialNetworkUserIdentityService socialNetworkUserIdentityService;

    public CustomersResource(CustomerFacade customerFacade,
                             RGPDCustomerService rgpdCustomerService,
                             CustomerGuard customerGuard,
                             CustomerOrderHistoryService customerOrderHistoryService, SocialNetworkUserIdentityService socialNetworkUserIdentityService) {
        this.customerFacade = customerFacade;
        this.rgpdCustomerService = rgpdCustomerService;
        this.customerGuard = customerGuard;
        this.customerOrderHistoryService = customerOrderHistoryService;
        this.socialNetworkUserIdentityService = socialNetworkUserIdentityService;
    }

    @GetMapping("/{uid}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable String uid) {
        Optional<CustomerDTO> customer = customerGuard.filterReadable(customerFacade.findByUid(uid));
        return customer
            .map(customerDTO -> ResponseUtils.getResponse(customerDTO, "GET Customer NOT FOUND by uid : " + uid))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping(path = "/{uid}/gdpr")
    public ResponseEntity<Void> delete(@PathVariable("uid") String uid) {
        customerGuard.filterReadable(customerFacade.findByUid(uid));
        if (!customerGuard.couldDelete()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        rgpdCustomerService.delete(uid);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CustomerDTO> getByEmailAndRealmCode(@RequestParam String email, @RequestParam("realm") String realm) {
        Optional<CustomerDTO> customer = customerGuard.filterReadable(customerFacade.findByEmailAndRealmCode(email, realm));
        return customer
            .map(customerDTO -> ResponseUtils.getResponse(customerDTO, "GET Customer NOT FOUND by email  : " + email))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }


    @GetMapping(path = "/{uid}/rgpd")
    public ResponseEntity<CustomerDataDTO> getDataRGPD(@PathVariable String uid) {

        Optional<CustomerDTO> customer = customerGuard.filterReadable(customerFacade.findByUid(uid));
        if(customer.isPresent()) {
            CustomerDataDTO result = rgpdCustomerService.getDataRGPD(customer.get().getEmail(), customer.get().getRealmCode());
            return ResponseEntity.ok().body(result);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<SearchResultCustomerDTO> search(@RequestBody PaginatedQuery query) {
        try {
            SearchResultCustomerDTO result = customerFacade.findCustomersByCriteria(query);
            return ResponseUtils.getResponse(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    @GetMapping("/{uid}/order")
    public ResponseEntity<CustomerOrderHistory> getCustomerOrderHistoryByCustomerUid(@PathVariable String uid) {
        CustomerOrderHistory history = customerOrderHistoryService.getByCustomerId(uid);
        return ResponseEntity
            .ok()
            .body(history);
    }


    @GetMapping("/{uid}/social")
    public ResponseEntity<SocialNetworkUserAssociations> getSocialAssociationByCustomerUid(@PathVariable String uid) {
        Optional<CustomerDTO> customer = customerGuard.filterReadable(customerFacade.findByUid(uid));
        if(customer.isPresent()) {
            SocialNetworkUserAssociations associations = socialNetworkUserIdentityService.getAssociations(customer.get().getRealmCode(),
                uid);
            return ResponseEntity
                .ok()
                .body(associations);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
