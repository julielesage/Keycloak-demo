package com.yrnet.spark.digitalaccount.api.functional.customer.v2;

import com.yrnet.spark.digitalaccount.api.functional.customer.guard.CustomerGuard;
import com.yrnet.spark.digitalaccount.api.functional.customer.v1.CustomerFacade;
import com.yrnet.spark.digitalaccount.api.functional.customer.v2.domain.PaginatedQuery;
import com.yrnet.spark.digitalaccount.dto.CustomerDTO;
import com.yrnet.spark.digitalaccount.dto.SearchResultCustomerDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomersResourceTest {

    @Mock
    private CustomerGuard customerGuard;
    @Mock
    private CustomerFacade customerFacade;

    @InjectMocks
    private CustomersResource customerResource;

    @Test
    public void search() {
        // MOCK
        final PaginatedQuery paginatedQuery = new PaginatedQuery();
        when(customerFacade.findCustomersByCriteria(paginatedQuery)).thenReturn(new SearchResultCustomerDTO());

        // WHEN
        ResponseEntity<SearchResultCustomerDTO> response = customerResource.search(paginatedQuery);

        // THEN
        verify(customerFacade).findCustomersByCriteria(paginatedQuery);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
