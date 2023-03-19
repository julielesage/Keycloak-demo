package com.yrnet.spark.sparkbxadmin.api.customer;

import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerDTO;
import com.yrnet.spark.loyaltyfacadeclient.dto.LoyaltyCustomerDTO;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.QueryCriteria;
import com.yrnet.spark.sparkbxadmin.converter.PaginatedQueryBuilder;
import com.yrnet.spark.sparkbxadmin.loyalty.LoyaltyFacade;
import com.yrnet.spark.sparkbxadmin.service.customer.CustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CustomerFacadeTest {

    final static String REALM_CODE = "YR-NL";
    final static String REALM_CODE_WITH_LOYALTY = "YR-FR";
    final static String CARD_CODE = "533035838838";
    final static String UID = "uid";
    final static String EMAIL = "test@test.te";
    final static String CSID = "csid";

    @Mock
    private CustomerService customerService;

    @Mock
    private LoyaltyFacade loyaltyFacade;

    @Mock
    private PaginatedQueryBuilder paginatedQueryBuilder;

    @InjectMocks
    private CustomerFacade customerFacade;

    @Captor
    private ArgumentCaptor<PaginatedQuery> captor;



    @Test
    public void get_customer_should_call_service_and_return_customer() {

        // DATA
        CustomerDTO expectedCustomerDTO= new CustomerDTO();
        expectedCustomerDTO.setRealmCode(REALM_CODE);

        // MOCK
        when(customerService.getCustomer(UID)).thenReturn(expectedCustomerDTO);

        // WHEN
        CustomerDTO customerDTO = customerFacade.getCustomer(UID);

        // THEN
        assertThat(customerDTO).isEqualTo(expectedCustomerDTO);
    }

    @Test
    public void getCustomers_should_return_customerService_answer() {
        // MOCK
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria realmCriteria = new QueryCriteria();
        realmCriteria.setField("realm");
        realmCriteria.setQuery(REALM_CODE);
        List<QueryCriteria> list = Arrays.asList(realmCriteria);
        paginatedQuery.setCriterias(list);

        when(customerService.getCustomers(paginatedQuery)).thenReturn(Optional.empty());
        when(paginatedQueryBuilder.init("20", "1")).thenReturn(paginatedQuery);
        when(loyaltyFacade.isActive(REALM_CODE)).thenReturn(false);

        // WHEN
        Optional<ResponseEnvelope> result = customerFacade.getCustomers(paginatedQuery);

        // THEN
        verify(customerService).getCustomers(paginatedQuery);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void getCustomers_should_return_optional_empty_with_wrong_loyaltyCard() {
        // MOCK
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria cardCriteria = new QueryCriteria();
        QueryCriteria realmCriteria = new QueryCriteria();
        cardCriteria.setQuery(CARD_CODE);
        cardCriteria.setField("loyaltyCard");
        realmCriteria.setField("realm");
        realmCriteria.setQuery(REALM_CODE_WITH_LOYALTY);
        List<QueryCriteria> list = Arrays.asList(cardCriteria, realmCriteria);
        paginatedQuery.setCriterias(list);

        when(customerService.getCustomers(paginatedQuery)).thenReturn(Optional.empty());
        when(paginatedQueryBuilder.init("20", "1")).thenReturn(paginatedQuery);
        when(loyaltyFacade.getLoyaltyCustomerByCard(paginatedQuery)).thenReturn(Optional.empty());
        when(loyaltyFacade.isActive(REALM_CODE_WITH_LOYALTY)).thenReturn(true);

        // WHEN
        Optional<ResponseEnvelope> result = customerFacade.getCustomers(paginatedQuery);

        // THEN
        verify(loyaltyFacade).getLoyaltyCustomerByCard(paginatedQuery);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void getCustomers_should_get_loyaltyCustomer_by_loyaltyCard_and_recall_customerService_with_csid() {

        // DATA
        PaginatedQuery firstPaginatedQuery = new PaginatedQuery();
        PaginatedQuery secondPaginatedQuery = new PaginatedQuery();
        QueryCriteria cardCriteria = new QueryCriteria();
        CustomerDTO customerDTO = new CustomerDTO();
        QueryCriteria realmCriteria = new QueryCriteria();
        QueryCriteria emailCriteria = new QueryCriteria();
        QueryCriteria csidCriteria = new QueryCriteria();
        LoyaltyCustomerDTO loyaltyCustomerDTO = new LoyaltyCustomerDTO();

        cardCriteria.setQuery(CARD_CODE);
        cardCriteria.setField("loyaltyCard");
        realmCriteria.setField("realm");
        realmCriteria.setQuery(REALM_CODE_WITH_LOYALTY);
        emailCriteria.setField("email");
        emailCriteria.setQuery(EMAIL);
        loyaltyCustomerDTO.setEmail(EMAIL);
        loyaltyCustomerDTO.setCsid(CSID);
        customerDTO.setRealmCode(REALM_CODE_WITH_LOYALTY);
        customerDTO.setEmail(EMAIL);
        csidCriteria.setField("csid");
        csidCriteria.setQuery(CSID);
        List<QueryCriteria> list = new ArrayList<>(Arrays.asList(cardCriteria, realmCriteria, emailCriteria));
        List<QueryCriteria> secondList = Arrays.asList(realmCriteria, emailCriteria, csidCriteria);
        firstPaginatedQuery.setCriterias(list);
        secondPaginatedQuery.setCriterias(secondList);

        // MOCK
        when(customerService.getCustomers(firstPaginatedQuery)).thenReturn(Optional.empty());
        when(customerService.getCustomers(secondPaginatedQuery)).thenReturn(Optional.empty());
        when(paginatedQueryBuilder.init("20", "1")).thenReturn(firstPaginatedQuery);
        when(loyaltyFacade.getLoyaltyCustomerByCard(firstPaginatedQuery)).thenReturn(Optional.ofNullable(loyaltyCustomerDTO));
        when(loyaltyFacade.isActive(REALM_CODE_WITH_LOYALTY)).thenReturn(true);

        // WHEN
        Optional<ResponseEnvelope> result = customerFacade.getCustomers(firstPaginatedQuery);

        // THEN
        verify(loyaltyFacade).getLoyaltyCustomerByCard(firstPaginatedQuery);
        verify(customerService, times(2)).getCustomers(captor.capture());
        assertThat(captor.getAllValues().get(0)).isEqualTo(firstPaginatedQuery);
        assertThat(captor.getAllValues().get(1).getCriterias().get(2).getQuery()).isEqualTo(CSID);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void getCustomers_should_get_loyaltyCustomer_but_render_empty_if_different_csid_as_criteria() {

        // DATA
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria cardCriteria = new QueryCriteria();
        CustomerDTO customerDTO = new CustomerDTO();
        QueryCriteria realmCriteria = new QueryCriteria();
        QueryCriteria emailCriteria = new QueryCriteria();
        LoyaltyCustomerDTO loyaltyCustomerDTO = new LoyaltyCustomerDTO();

        cardCriteria.setQuery(CARD_CODE);
        cardCriteria.setField("loyaltyCard");
        realmCriteria.setField("realm");
        realmCriteria.setQuery(REALM_CODE_WITH_LOYALTY);
        emailCriteria.setField("email");
        emailCriteria.setQuery(EMAIL);
        loyaltyCustomerDTO.setEmail("different email");
        loyaltyCustomerDTO.setCsid(CSID);
        customerDTO.setRealmCode(REALM_CODE_WITH_LOYALTY);
        customerDTO.setEmail(EMAIL);
        List<QueryCriteria> list = new ArrayList<>(Arrays.asList(cardCriteria, realmCriteria, emailCriteria));
        paginatedQuery.setCriterias(list);

        // MOCK
        when(customerService.getCustomers(paginatedQuery)).thenReturn(Optional.empty());
        when(paginatedQueryBuilder.init("20", "1")).thenReturn(paginatedQuery);
        when(loyaltyFacade.getLoyaltyCustomerByCard(paginatedQuery)).thenReturn(Optional.ofNullable(loyaltyCustomerDTO));
        when(loyaltyFacade.isActive(REALM_CODE_WITH_LOYALTY)).thenReturn(true);

        // WHEN
        Optional<ResponseEnvelope> result = customerFacade.getCustomers(paginatedQuery);

        // THEN
        verify(loyaltyFacade).getLoyaltyCustomerByCard(paginatedQuery);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void isCompatibleWithCsidQuery_should_return_true_if_no_csid_query() {
        // MOCK
        LoyaltyCustomerDTO loyaltyCustomerDTO = new LoyaltyCustomerDTO();
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.setField("lastname");
        paginatedQuery.setCriterias(Arrays.asList(queryCriteria));

        // WHEN/THEN
        assertThat(customerFacade.isCompatibleWithCsidQuery(loyaltyCustomerDTO, paginatedQuery)).isTrue();
    }

    @Test
    public void isCompatibleWithCsidQuery_should_return_false_if_csid_query_is_different() {
        // MOCK
        LoyaltyCustomerDTO loyaltyCustomerDTO = new LoyaltyCustomerDTO();
        loyaltyCustomerDTO.setCsid("loyaltyCsid");
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria csidCriteria = new QueryCriteria();
        csidCriteria.setField("csid");
        csidCriteria.setQuery("csidQuery");
        paginatedQuery.setCriterias(Arrays.asList(csidCriteria));

        // WHEN/THEN
        assertThat(customerFacade.isCompatibleWithCsidQuery(loyaltyCustomerDTO, paginatedQuery)).isFalse();
    }

    @Test
    public void isCompatibleWithCsidQuery_should_return_true_if_same_csid() {
        // MOCK
        LoyaltyCustomerDTO loyaltyCustomerDTO = new LoyaltyCustomerDTO();
        loyaltyCustomerDTO.setCsid("csidQuery");
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria csidCriteria = new QueryCriteria();
        csidCriteria.setField("csid");
        csidCriteria.setQuery("csidQuery");
        paginatedQuery.setCriterias(Arrays.asList(csidCriteria));

        // WHEN/THEN
        assertThat(customerFacade.isCompatibleWithCsidQuery(loyaltyCustomerDTO, paginatedQuery)).isTrue();
    }
}
