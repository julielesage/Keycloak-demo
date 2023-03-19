package com.yrnet.spark.sparkbxadmin.api.customer;

import com.yrnet.spark.bxadmin.api.CustomerApi;
import com.yrnet.spark.bxadmin.model.AddressResult;
import com.yrnet.spark.bxadmin.model.Consent;
import com.yrnet.spark.bxadmin.model.Customer;
import com.yrnet.spark.bxadmin.model.Logs;
import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.digitalaccountclient.dto.consent.ConsentDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.AddressDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerOrderHistory;
import com.yrnet.spark.sparkbxadmin.digaccclient.dto.TaskDTO;
import com.yrnet.spark.digitalaccountclient.exception.ApiException;
import com.yrnet.spark.sparkbxadmin.api.response.AddressResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.api.response.AddressResultResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.api.response.ConsentResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.api.response.CustomerResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.api.response.LogsResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.api.response.TasksResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.converter.PaginatedQueryBuilder;
import com.yrnet.spark.sparkbxadmin.digaccclient.dto.SocialNetworkUserAssociations;
import com.yrnet.spark.sparkbxadmin.keycloak.KeycloakTokenHelper;
import com.yrnet.spark.sparkbxadmin.mapper.AddressDTOToAddressMapper;
import com.yrnet.spark.sparkbxadmin.mapper.AddressDTOToAddressResultMapper;
import com.yrnet.spark.sparkbxadmin.mapper.ConsentDTOToConsentMapper;
import com.yrnet.spark.sparkbxadmin.mapper.CustomerDTOToCustomerMapper;
import com.yrnet.spark.sparkbxadmin.populator.CustomerOrderHistoryPopulator;
import com.yrnet.spark.sparkbxadmin.populator.CustomerSocialIdentityPopulator;
import com.yrnet.spark.sparkbxadmin.populator.LogsMyConsumerInformationPopulator;
import com.yrnet.spark.sparkbxadmin.service.audit.AuditService;
import com.yrnet.spark.sparkbxadmin.service.customer.AddressService;
import com.yrnet.spark.sparkbxadmin.service.customer.ConsentService;
import com.yrnet.spark.sparkbxadmin.service.customer.CustomerService;
import com.yrnet.spark.sparkbxadmin.service.customer.HtmlFileGenerator;
import com.yrnet.spark.sparkbxadmin.service.customer.TaskService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomerResource implements CustomerApi {

    private final CustomerService customerService;
    private final CustomerFacade customerFacade;
    private final AddressService addressService;
    private final ConsentService consentService;
    private final TaskService taskService;
    private final CustomerDTOToCustomerMapper customerDTOToCustomerMapper;
    private final AddressDTOToAddressMapper addressDTOToAddressMapper;
    private final AddressDTOToAddressResultMapper addressDTOToAddressResultMapper;
    private final ConsentDTOToConsentMapper consentDTOToConsentMapper;
    private final AuditService auditService;
    private final CustomerOrderHistoryPopulator customerOrderHistoryPopulator;
    private final CustomerSocialIdentityPopulator customerSocialIdentityPopulator;
    private final LogsMyConsumerInformationPopulator logsMyConsumerInformationPopulator;
    private final PaginatedQueryBuilder paginatedQueryBuilder;
    private final HtmlFileGenerator htmlFileGenerator;


    public CustomerResource(CustomerService customerService,
                            CustomerFacade customerFacade,
                            AddressService addressService,
                            ConsentService consentService,
                            AuditService auditService,
                            TaskService taskService,
                            CustomerDTOToCustomerMapper customerDTOToCustomerMapper,
                            AddressDTOToAddressMapper addressDTOToAddressMapper,
                            AddressDTOToAddressResultMapper addressDTOToAddressResultMapper,
                            ConsentDTOToConsentMapper consentDTOToConsentMapper,
                            CustomerOrderHistoryPopulator customerOrderHistoryPopulator,
                            CustomerSocialIdentityPopulator customerSocialIdentityPopulator,
                            LogsMyConsumerInformationPopulator logsMyConsumerInformationPopulator,
                            PaginatedQueryBuilder paginatedQueryBuilder,
                            HtmlFileGenerator htmlFileGenerator) {
        this.customerService = customerService;
        this.customerFacade = customerFacade;
        this.addressService = addressService;
        this.consentService = consentService;
        this.auditService = auditService;
        this.taskService = taskService;
        this.customerDTOToCustomerMapper = customerDTOToCustomerMapper;
        this.addressDTOToAddressMapper = addressDTOToAddressMapper;
        this.addressDTOToAddressResultMapper = addressDTOToAddressResultMapper;
        this.consentDTOToConsentMapper = consentDTOToConsentMapper;
        this.customerOrderHistoryPopulator = customerOrderHistoryPopulator;
        this.customerSocialIdentityPopulator = customerSocialIdentityPopulator;
        this.logsMyConsumerInformationPopulator = logsMyConsumerInformationPopulator;
        this.paginatedQueryBuilder = paginatedQueryBuilder;
        this.htmlFileGenerator = htmlFileGenerator;
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(@ApiParam(value = "Customer UID", required = true) @PathVariable("uid") String uid) {
        try {
            customerService.deleteCustomerGDPR(uid);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ApiException e) {
            log.error("Error while delete customer for uid {} : ", uid, e);
            return new ResponseEntity<>(HttpStatus.valueOf(e.getStatus()));
        }
    }

    @Override
    public ResponseEntity<ResponseEnvelope> customerLookup(
            @NotNull @ApiParam(value = "Market context", required = true) @Valid @RequestParam(value = "realm", required = true) String realm,
            @NotNull @ApiParam(value = "page size", required = true) @Valid @RequestParam(value = "pageSize", required = true) String pageSize,
            @NotNull @ApiParam(value = "page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) String pageNumber,
            @ApiParam(value = "Customer email") @Valid @RequestParam(value = "email", required = false) String email,
            @ApiParam(value = "customer firstname") @Valid @RequestParam(value = "firstname", required = false) String firstname,
            @ApiParam(value = "customer lastname") @Valid @RequestParam(value = "lastname", required = false) String lastname,
            @ApiParam(value = "customer registrationemail") @Valid @RequestParam(value = "registrationemail", required = false) String registrationemail,
            @ApiParam(value = "customer pseudo") @Valid @RequestParam(value = "pseudo", required = false) String pseudo,
            @ApiParam(value = "customer uid") @Valid @RequestParam(value = "uid", required = false) String uid,
            @ApiParam(value = "customer csid") @Valid @RequestParam(value = "csid", required = false) String csid,
            @ApiParam(value = "customer loyalty card number") @Valid @RequestParam(value = "loyaltycard", required = false) String loyaltycard,
            @ApiParam(value = "customer vad id") @Valid @RequestParam(value = "idvad", required = false) String idvad) {

        PaginatedQuery paginatedQuery = paginatedQueryBuilder.init(pageSize, pageNumber);
        paginatedQueryBuilder.addRealm(paginatedQuery, realm);
        paginatedQueryBuilder.addEmail(paginatedQuery, email);
        paginatedQueryBuilder.addRegistrationEmail(paginatedQuery, registrationemail);
        paginatedQueryBuilder.addFirstName(paginatedQuery, firstname);
        paginatedQueryBuilder.addLastName(paginatedQuery, lastname);
        paginatedQueryBuilder.addPseudo(paginatedQuery, pseudo);
        paginatedQueryBuilder.addUid(paginatedQuery, uid);
        paginatedQueryBuilder.addCsid(paginatedQuery, csid);
        paginatedQueryBuilder.addLoyaltyCard(paginatedQuery, loyaltycard);
        paginatedQueryBuilder.addIdVad(paginatedQuery, idvad);

        Optional<ResponseEnvelope> response = customerFacade.getCustomers(paginatedQuery);

        return response
                .map(r -> ResponseEntity.status(Integer.parseInt(r.getMessages().get(0).getCode())).body(r))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @Override
    public ResponseEntity<ResponseEnvelope> getCustomer(@ApiParam(value = "Customer UID", required = true)
                                                        @PathVariable("uid") String uid) {
        CustomerOrderHistory customerOrderHistory = customerService.getCustomerOrderHistory(uid);
        SocialNetworkUserAssociations socialAssociationByCustomerUid = customerService.getSocialAssociationByCustomerUid(uid);
        return Optional.ofNullable(customerFacade.getCustomer(uid))
                .map(customerDTOToCustomerMapper::customerDTOToCustomer)
                .map(e -> customerOrderHistoryPopulator.populate(e, customerOrderHistory))
                .map(e -> customerSocialIdentityPopulator.populate(e, socialAssociationByCustomerUid))
                .map(CustomerResponseEnvelope::new)
                .map(r -> (ResponseEnvelope) r)
                .map(r -> ResponseEntity.ok().body(r))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    @ApiOperation(value = "generate_GDPR_customer_legacy",
            nickname = "getCustomerGDPR",
            notes = "send an html designed file with all gdpr info",
            response = String.class, tags = {"customer"})
    @GetMapping(value = "/customer/{uid}/gdpr/file", produces = {"text/html"})
    public ResponseEntity<InputStreamResource> getCustomerGDPRFile(@ApiParam(value = "Customer UID", required = true)
                                                                   @PathVariable("uid") String uid) throws IOException {

        if (KeycloakTokenHelper.isNotAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomerOrderHistory customerOrderHistory = customerService.getCustomerOrderHistory(uid);
        SocialNetworkUserAssociations socialAssociationByCustomerUid = customerService.getSocialAssociationByCustomerUid(uid);
        Optional<Customer> customer = Optional.ofNullable(customerService.getCustomer(uid)).map(customerDTOToCustomerMapper::customerDTOToCustomer)
                .map(e -> customerOrderHistoryPopulator.populate(e, customerOrderHistory))
                .map(e -> customerSocialIdentityPopulator.populate(e, socialAssociationByCustomerUid));
        if (customer.isPresent()) {
            File file = htmlFileGenerator.generateHTMLFileForGdpr(customer.get());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .contentType(MediaType.TEXT_HTML)
                    .body(resource);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }


    @Override
    public ResponseEntity<ResponseEnvelope> getCustomerAddresses(
            @ApiParam(value = "Customer UID", required = true) @PathVariable("uid") String uid) {

        List<AddressDTO> addresses = addressService.getAddresses(uid);
        if (CollectionUtils.isNotEmpty(addresses)) {
            ArrayList<AddressResult> collect = (ArrayList<AddressResult>) addresses.stream()
                    .map(addressDTOToAddressResultMapper::addressDTOToAddressResult).collect(Collectors.toList());

            return Optional.of(collect)
                    .map(AddressResultResponseEnvelope::new)
                    .map(r -> (ResponseEnvelope) r)
                    .map(r -> ResponseEntity.ok().body(r))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @Override
    public ResponseEntity<ResponseEnvelope> getCustomerAddress(
            @ApiParam(value = "Customer UID", required = true) @PathVariable("uid") String uid,
            @ApiParam(value = "address ID", required = true) @PathVariable("id") String id) {

        return Optional.ofNullable(addressService.getAddressById(uid, id))
                .map(addressDTOToAddressMapper::addressDTOToAddress)
                .map(AddressResponseEnvelope::new)
                .map(r -> (ResponseEnvelope) r)
                .map(r -> ResponseEntity.ok().body(r))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @Override
    public ResponseEntity<ResponseEnvelope> getCustomerConsents(
            @ApiParam(value = "Customer UID", required = true) @PathVariable("uid") String uid) {

        List<ConsentDTO> consents = consentService.getConsents(uid);
        if (CollectionUtils.isNotEmpty(consents)) {
            ArrayList<Consent> collect = (ArrayList<Consent>) consents.stream()
                    .map(consentDTOToConsentMapper::consentDTOToConsent).collect(Collectors.toList());

            return Optional.of(collect)
                    .map(ConsentResponseEnvelope::new)
                    .map(r -> (ResponseEnvelope) r)
                    .map(r -> ResponseEntity.ok().body(r))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));

        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @Override
    public ResponseEntity<ResponseEnvelope> getCustomerLogs(
            @ApiParam(value = "Customer UID", required = true) @PathVariable("uid") String uid,
            @NotNull @ApiParam(value = "page size", required = true) @Valid @RequestParam(value = "pageSize", required = true) String pageSize,
            @NotNull @ApiParam(value = "page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) String pageNumber,
            @NotNull @ApiParam(value = "Customer email", required = true) @Valid @RequestParam(value = "email", required = true) String email,
            @NotNull @ApiParam(value = "Market context", required = true) @Valid @RequestParam(value = "realm", required = true) String realm) {
        Logs logs = auditService.search(uid, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), realm, email);
        if (Objects.nonNull(logs)) {
            CustomerDTO customer = customerService.getCustomer(uid);
            if (Objects.nonNull(customer)) {
                logs.setLegacyId(Optional.ofNullable(customer.getLegacyId()).map(Long::valueOf).orElse(null));
                if (Objects.nonNull(customer.getCreationDate())) {
                    OffsetDateTime creationDate = OffsetDateTime.ofInstant(customer.getCreationDate(), ZoneId.systemDefault());
                    logs.setCreationDate(creationDate);
                }
                if (Objects.nonNull(customer.getMyCustomerLastUpdateDate())) {
                    OffsetDateTime lastUpdateDate = OffsetDateTime.ofInstant(customer.getMyCustomerLastUpdateDate().toInstant(), ZoneId.systemDefault());
                    logs.setUpdateDate(lastUpdateDate);
                }
                if (Objects.nonNull(customer.getLastLoginDate())) {
                    OffsetDateTime lastLoginDate = OffsetDateTime.ofInstant(customer.getLastLoginDate().toInstant(), ZoneId.systemDefault());
                    logs.setLastLoginDate(lastLoginDate);
                }
                logs.setMyConsumerInformation(logsMyConsumerInformationPopulator.populate(customer));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return Optional.of(logs)
                    .map(LogsResponseEnvelope::new)
                    .map(r -> (ResponseEnvelope) r)
                    .map(r -> ResponseEntity.ok().body(r))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @Override
    public ResponseEntity<ResponseEnvelope> getCustomerTasks(
            @ApiParam(value = "Customer UID", required = true) @PathVariable("uid") String uid) {

            TaskDTO tasks = taskService.getJobsByCustomerUid(uid);
            return Optional.of(tasks)
                    .map(TasksResponseEnvelope::new)
                    .map(r -> (ResponseEnvelope) r)
                    .map(r -> ResponseEntity.ok().body(r))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));

    }

}
