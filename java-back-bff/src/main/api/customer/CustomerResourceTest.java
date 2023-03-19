package com.yrnet.spark.sparkbxadmin.api.customer;

import com.yrnet.spark.bxadmin.model.Address;
import com.yrnet.spark.bxadmin.model.AddressResult;
import com.yrnet.spark.bxadmin.model.Customer;
import com.yrnet.spark.bxadmin.model.CustomerResult;
import com.yrnet.spark.bxadmin.model.DataCustomerResult;
import com.yrnet.spark.bxadmin.model.Logs;
import com.yrnet.spark.bxadmin.model.LogsMyConsumerInformation;
import com.yrnet.spark.bxadmin.model.Message;
import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.digitalaccountclient.dto.consent.ConsentDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.AddressDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerOrderHistory;
import com.yrnet.spark.digitalaccountclient.dto.job.JobDTO;
import com.yrnet.spark.digitalaccountclient.dto.job.RCSynchroRequestDTO;
import com.yrnet.spark.sparkbxadmin.digaccclient.dto.TaskDTO;
import com.yrnet.spark.digitalaccountclient.exception.ApiException;
import com.yrnet.spark.sparkbxadmin.api.response.GenericResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.converter.PaginatedQueryBuilder;
import com.yrnet.spark.sparkbxadmin.digaccclient.dto.SocialNetworkUserAssociations;
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
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CustomerResourceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private ConsentService consentService;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerFacade customerFacade;

    @Mock
    private CustomerOrderHistoryPopulator customerOrderHistoryPopulator;

    @Mock
    private CustomerSocialIdentityPopulator customerSocialIdentityPopulator;

    @Mock
    private LogsMyConsumerInformationPopulator logsMyConsumerInformationPopulator;

    @Mock
    private CustomerDTOToCustomerMapper customerDTOToCustomerMapper;

    @Mock
    private ConsentDTOToConsentMapper consentDTOToConsentMapper;

    @Mock
    private PaginatedQueryBuilder paginatedQueryBuilder;

    @Mock
    private AddressService addressService;

    @Mock
    private AuditService auditService;

    @Mock
    private AddressDTOToAddressResultMapper addressDTOToAddressResultMapper;

    @Mock
    private AddressDTOToAddressMapper addressDTOToAddressMapper;

    @Mock
    private HtmlFileGenerator htmlFileGenerator;

    @InjectMocks
    private CustomerResource customerResource;

    @Test
    public void get_customer() {

        // MOCK
        CustomerOrderHistory customerOrderHistory = new CustomerOrderHistory();
        SocialNetworkUserAssociations socialAssociationByCustomerUid = new SocialNetworkUserAssociations();
        CustomerDTO customerDTO = buildCustomerDTO();
        Customer customer = new Customer();
        when(customerService.getCustomerOrderHistory("fake uid")).thenReturn(customerOrderHistory);
        when(customerService.getSocialAssociationByCustomerUid("fake uid")).thenReturn(socialAssociationByCustomerUid);
        when(customerFacade.getCustomer("fake uid")).thenReturn(customerDTO);
        when(customerDTOToCustomerMapper.customerDTOToCustomer(customerDTO)).thenReturn(customer);
        when(customerOrderHistoryPopulator.populate(customer, customerOrderHistory)).thenReturn(null);
        when(customerSocialIdentityPopulator.populate(customer, socialAssociationByCustomerUid)).thenReturn(null);
        // WHEN
        ResponseEntity<ResponseEnvelope> response = customerResource.getCustomer("fake uid");

        // THEN
        verify(customerService).getCustomerOrderHistory("fake uid");
        verify(customerService).getSocialAssociationByCustomerUid("fake uid");
        verify(customerFacade).getCustomer("fake uid");
        assertThat(response).isNotNull();
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void getCustomerTasks_with_manager_access() {

        // DATA
        JobDTO job = new JobDTO();
        JobDTO job1 = new JobDTO();
        JobDTO job2 = new JobDTO();
        RCSynchroRequestDTO rcSynchroRequestDTO = new RCSynchroRequestDTO();
        job.setType("SEND_SALES_FORCE_JOB");
        job1.setType("SEND_LEADS_JOB");
        job2.setType("SEND_LEADS_JOB");
        rcSynchroRequestDTO.setEmail("test@test.test");
        List<JobDTO> SFList = Arrays.asList(job);
        List<JobDTO> leadsList = Arrays.asList(job1, job2);
        List<RCSynchroRequestDTO> rcList = Arrays.asList(rcSynchroRequestDTO);
        TaskDTO taskDTO = TaskDTO.builder()
                .salesForceJobs(SFList)
                .rcSynchroRequests(rcList)
                .leadsJobs(leadsList)
                .build();

        // MOCK
        when(taskService.getJobsByCustomerUid("fake uid")).thenReturn(taskDTO);

        // WHEN
        ResponseEntity<ResponseEnvelope> response = customerResource.getCustomerTasks("fake uid");

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void getCustomerTasks_with_manager_access_without_jobs_should_return_no_content() {

        // MOCK
        when(taskService.getJobsByCustomerUid("fake uid")).thenReturn(TaskDTO.builder().build());

        // WHEN
        ResponseEntity<ResponseEnvelope> response = customerResource.getCustomerTasks("fake uid");

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private CustomerDTO buildCustomerDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        long id = 12345L;
        customerDTO.setIdRetail("idRetail");
        customerDTO.setIdVAD("idVad");
        customerDTO.setCsid("1234567890");
        customerDTO.setUid("121234567890UHBN");
        customerDTO.setId(id);
        customerDTO.setLastname("lesage");
        customerDTO.setFirstname("julie");
        return customerDTO;
    }

    @Test
    public void customerLookup_should_return_customer() {

        PaginatedQuery paginatedQuery = new PaginatedQuery();
        when(paginatedQueryBuilder.init("20", "1")).thenReturn(paginatedQuery);

        final GenericResponseEnvelope response = buildResponse();

        when(customerFacade.getCustomers(paginatedQuery)).thenReturn(Optional.of(response));

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.customerLookup("YR-FR", "20", "1", "test@test.com", "firstName", "lastName", "test@test.com", "pseudo", "uid", "csid", "loyaltycard", "idvad");

        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void customerLookup_should_return_error_when_customer_not_found() {

        PaginatedQuery paginatedQuery = new PaginatedQuery();
        when(paginatedQueryBuilder.init("20", "1")).thenReturn(paginatedQuery);

        final GenericResponseEnvelope response = buildCUstomerNotFoundResponse();

        when(customerFacade.getCustomers(paginatedQuery)).thenReturn(Optional.of(response));


        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.customerLookup("YR-FR", "20", "1", "test@test.com", "firstName", "lastName", "test@test.com", "pseudo", "uid", "csid", "loyaltycard", "idvad");
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEnvelopeResponseEntity.getStatusCode());
    }

    private GenericResponseEnvelope buildResponse() {
        final DataCustomerResult dataCustomerResult = new DataCustomerResult();
        List<CustomerResult> customerResult = new ArrayList<>();
        CustomerResult customer = new CustomerResult();
        customer.setId(12345600L);
        customer.setEmail("test@test.com");
        customerResult.add(customer);
        dataCustomerResult.setResults(customerResult);


        final GenericResponseEnvelope response = new GenericResponseEnvelope(dataCustomerResult);
        List<Message> messages = new ArrayList<>();
        Message m = new Message();
        m.setCode("200");
        messages.add(m);
        response.setMessages(messages);
        return response;
    }

    private GenericResponseEnvelope buildCUstomerNotFoundResponse() {

        final GenericResponseEnvelope response = new GenericResponseEnvelope(null);
        List<Message> messages = new ArrayList<>();
        Message m = new Message();
        m.setCode("404");
        messages.add(m);
        response.setMessages(messages);
        return response;
    }

    @Test
    public void getCustomerAddresses_should_return_ok() {

        String customerUid = "customer-uid";

        List<AddressDTO> addresses = buildCustomerAddresses();

        when(addressService.getAddresses(customerUid)).thenReturn(addresses);
        when(addressDTOToAddressResultMapper.addressDTOToAddressResult(addresses.get(0))).thenReturn(new AddressResult());

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerAddresses(customerUid);
        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerAddresses_should_return_no_content() {

        String customerUid = "customer-uid";

        when(addressService.getAddresses(customerUid)).thenReturn(null);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerAddresses(customerUid);
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerAddress_should_return_ok() {

        String customerUid = "customer-uid";
        String addressId = "address-id";

        AddressDTO addressDTO = new AddressDTO();

        when(addressService.getAddressById(customerUid, addressId)).thenReturn(addressDTO);
        when(addressDTOToAddressMapper.addressDTOToAddress(addressDTO)).thenReturn(new Address());

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerAddress(customerUid, addressId);
        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerAddress_should_return_no_content() {

        String customerUid = "customer-uid";
        String addressId = "address-id";

        when(addressService.getAddressById(customerUid, addressId)).thenReturn(null);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerAddress(customerUid, addressId);
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEnvelopeResponseEntity.getStatusCode());
    }

    private List<AddressDTO> buildCustomerAddresses() {
        return Arrays.asList(new AddressDTO(), new AddressDTO(), new AddressDTO());
    }

    @Test
    public void getCustomerLogs_should_return_ok() {

        //MOCK
        String customerUid = "customer-uid";
        String pageNumber = "2";
        String pageSize = "2";
        String realm = "realm";
        String email = "toto@yopmail.com";
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setUid(customerUid);
        customerDTO.setLegacyId("123");
        customerDTO.setCreationDate(Instant.now());
        customerDTO.setMyCustomerLastUpdateDate(ZonedDateTime.now());
        customerDTO.setLastLoginDate(ZonedDateTime.now());


        //WHEN
        when(auditService.search(customerUid, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), realm, email)).thenReturn(new Logs());
        when(customerService.getCustomer(customerUid)).thenReturn(customerDTO);

        LogsMyConsumerInformation logsMyConsumerInformation = new LogsMyConsumerInformation();
        when(logsMyConsumerInformationPopulator.populate(customerDTO)).thenReturn(logsMyConsumerInformation);

        //THEN
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerLogs(customerUid, pageSize, pageNumber, email, realm);
        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerLogs_without_dates_should_return_ok() {

        //MOCK
        String customerUid = "customer-uid";
        String pageNumber = "2";
        String pageSize = "2";
        String realm = "realm";
        String email = "toto@yopmail.com";
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setUid(customerUid);
        customerDTO.setLegacyId("123");
        customerDTO.setCreationDate(null);
        customerDTO.setMyCustomerLastUpdateDate(null);
        customerDTO.setLastLoginDate(null);


        //WHEN
        when(auditService.search(customerUid, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), realm, email)).thenReturn(new Logs());
        when(customerService.getCustomer(customerUid)).thenReturn(customerDTO);

        LogsMyConsumerInformation logsMyConsumerInformation = new LogsMyConsumerInformation();
        when(logsMyConsumerInformationPopulator.populate(customerDTO)).thenReturn(logsMyConsumerInformation);

        //THEN
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerLogs(customerUid, pageSize, pageNumber, email, realm);
        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerLogs_without_logs_should_return_no_content() {

        //MOCK
        String customerUid = "customer-uid";
        String pageNumber = "2";
        String pageSize = "2";
        String realm = "realm";
        String email = "toto@yopmail.com";
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setUid(customerUid);
        customerDTO.setLegacyId("123");
        customerDTO.setCreationDate(null);
        customerDTO.setMyCustomerLastUpdateDate(null);
        customerDTO.setLastLoginDate(null);


        //WHEN
        when(auditService.search(customerUid, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), realm, email)).thenReturn(null);

        //THEN
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerLogs(customerUid, pageSize, pageNumber, email, realm);
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerLogs_without_customer_should_return_no_content() {

        //MOCK
        String customerUid = "customer-uid";
        String pageNumber = "2";
        String pageSize = "2";
        String realm = "realm";
        String email = "toto@yopmail.com";

        //WHEN
        when(auditService.search(customerUid, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), realm, email)).thenReturn(new Logs());
        when(customerService.getCustomer(customerUid)).thenReturn(null);

        //THEN
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerLogs(customerUid, pageSize, pageNumber, email, realm);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerConsents_should_return_ok() {

        //MOCK
        String customerUid = "customer-uid";

        //WHEN
        when(consentService.getConsents(customerUid)).thenReturn(buildCustomerConsent());

        //THEN
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerConsents(customerUid);
        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getCustomerConsents_when_no_consents_should_return_no_content() {

        //MOCK
        String customerUid = "customer-uid";

        //WHEN
        when(consentService.getConsents(customerUid)).thenReturn(null);

        //THEN
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = customerResource.getCustomerConsents(customerUid);
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEnvelopeResponseEntity.getStatusCode());
    }

    private List<ConsentDTO> buildCustomerConsent() {
        return Arrays.asList(new ConsentDTO(), new ConsentDTO(), new ConsentDTO());
    }

    @Test
    public void deleteCustomerShouldReturnExceptionWhenError() {

        ApiException apiException = new ApiException(404, "test");
        doThrow(apiException).when(customerService).deleteCustomerGDPR("uid");
        ResponseEntity<Void> response = customerResource.deleteCustomer("uid");
        Assert.assertTrue(response.getStatusCode().value() == 404);

    }

    @Test
    public void deleteCustomerShouldReturn200() {

        ApiException apiException = new ApiException(404, "test");
        doNothing().when(customerService).deleteCustomerGDPR("uid");
        ResponseEntity<Void> response = customerResource.deleteCustomer("uid");
        Assert.assertTrue(response.getStatusCode().value() == 200);

    }


    private void buildSecurityContext() {
        KeycloakAuthenticationToken authentication = mock(KeycloakAuthenticationToken.class);
        OidcKeycloakAccount account = mock(OidcKeycloakAccount.class);
        KeycloakSecurityContext securitycontext = mock(KeycloakSecurityContext.class);
        AccessToken token = new AccessToken();
        token.id("id");
        token.issuer("issuer");
        token.subject("subject");
        token.exp(100000L);

        when(securitycontext.getToken()).thenReturn(token);
        when(account.getKeycloakSecurityContext()).thenReturn(securitycontext);
        Mockito.when(authentication.getAccount()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }


    @Test
    public void getCustomerGDPRFileShouldGenerateFile() throws IOException {

        buildSecurityContext();
        // MOCK
        CustomerOrderHistory customerOrderHistory = new CustomerOrderHistory();
        SocialNetworkUserAssociations socialAssociationByCustomerUid = new SocialNetworkUserAssociations();
        CustomerDTO customerDTO = buildCustomerDTO();
        Customer customer = new Customer();
        customer.setId(10000L);
        when(customerService.getCustomerOrderHistory("fake uid")).thenReturn(customerOrderHistory);
        when(customerService.getSocialAssociationByCustomerUid("fake uid")).thenReturn(socialAssociationByCustomerUid);
        when(customerService.getCustomer("fake uid")).thenReturn(customerDTO);
        when(customerFacade.getCustomer("fake uid")).thenReturn(customerDTO);
        when(customerDTOToCustomerMapper.customerDTOToCustomer(customerDTO)).thenReturn(customer);

        when(htmlFileGenerator.generateHTMLFileForGdpr(customer)).thenReturn(getFile());
        when(customerOrderHistoryPopulator.populate(customer, customerOrderHistory)).thenReturn(customer);
        when(customerSocialIdentityPopulator.populate(customer, socialAssociationByCustomerUid)).thenReturn(customer);
        ResponseEntity<InputStreamResource> response = customerResource.getCustomerGDPRFile("fake uid");
        Assert.assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    public void getCustomerGDPRFileShouldReturn201WhenNoCustomer() throws IOException {
        buildSecurityContext();
        // MOCK
        CustomerOrderHistory customerOrderHistory = new CustomerOrderHistory();
        SocialNetworkUserAssociations socialAssociationByCustomerUid = new SocialNetworkUserAssociations();
        CustomerDTO customerDTO = buildCustomerDTO();
        Customer customer = new Customer();
        customer.setId(10000L);
        when(customerService.getCustomerOrderHistory("fake uid")).thenReturn(customerOrderHistory);
        when(customerService.getSocialAssociationByCustomerUid("fake uid")).thenReturn(socialAssociationByCustomerUid);
        when(customerService.getCustomer("fake uid")).thenReturn(null);
        when(customerFacade.getCustomer("fake uid")).thenReturn(null);
        when(customerDTOToCustomerMapper.customerDTOToCustomer(customerDTO)).thenReturn(null);

        when(htmlFileGenerator.generateHTMLFileForGdpr(customer)).thenReturn(getFile());
        when(customerOrderHistoryPopulator.populate(customer, customerOrderHistory)).thenReturn(customer);
        when(customerSocialIdentityPopulator.populate(customer, socialAssociationByCustomerUid)).thenReturn(customer);
        ResponseEntity<InputStreamResource> response = customerResource.getCustomerGDPRFile("fake uid");
        Assert.assertEquals(response.getStatusCode().value(), 204);
    }


    private File getFile() throws IOException {
        File baseDir = new File("testFolder/");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        FileUtils.cleanDirectory(new File("testFolder/"));


        File f = new File("testFolder", FilenameUtils.getName("test.html"));
        f.createNewFile();
        return f;
    }
}
