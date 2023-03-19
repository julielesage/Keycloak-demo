package com.yrnet.spark.sparkbxadmin.response;

import com.yrnet.spark.bxadmin.model.CustomerResult;
import com.yrnet.spark.bxadmin.model.DataCustomerResult;
import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.SearchResultCustomerDTO;
import com.yrnet.spark.sparkbxadmin.api.response.GenericResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.converter.ResponseEnvelopeConverter;
import com.yrnet.spark.sparkbxadmin.mapper.CustomerDTOToCustomerResultMapper;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResponseEnvelopeConverterTest {

    @InjectMocks
    ResponseEnvelopeConverter responseEnvelopeConverter;

    @Mock
    CustomerDTOToCustomerResultMapper customerDTOToCustomerResultMapper;

    @Test
    public void covertSouldConvertresultCustomerDtoTOresponseEnvelope() {
        final PaginatedQuery paginatedQuery = new PaginatedQuery();
        final CustomerDTO customerDTO = new CustomerDTO();
        final SearchResultCustomerDTO searchResultCustomerDTO = SearchResultCustomerDTO
                .builder()
                .maxResult(1)
                .customers(Arrays.asList(customerDTO))
                .build();

        CustomerResult customerResult= new CustomerResult();
        customerResult.setEmail("test@test.com");
        Mockito.when(customerDTOToCustomerResultMapper.customerDTOToCustomerResult(customerDTO)).thenReturn(customerResult);
        ResponseEnvelope result = responseEnvelopeConverter.convert(searchResultCustomerDTO, paginatedQuery);
        Assert.assertNotNull(result);
        Assert.assertNotNull(((DataCustomerResult) ((GenericResponseEnvelope) result).getData()).getResults());
    }


    @Test
    public void covertSouldConvertMessageStatusToResponseEnvelopeOk() {
        ResponseEnvelope result = responseEnvelopeConverter.convert(200);
        Assert.assertNotNull(result);
        Assert.assertEquals(((GenericResponseEnvelope) result).getMessages().stream().findFirst().get().getCode(),"200");
    }

    @Test
    public void covertSouldConvertMessageToResponseEnvelope403() {
        ResponseEnvelope result = responseEnvelopeConverter.convert(403);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getMessages().get(0).getMessage().contains("You don't have right to perform this action."));
        Assert.assertEquals(((GenericResponseEnvelope) result).getMessages().stream().findFirst().get().getCode(),"403");
    }
    @Test
    public void covertSouldConvertMessageToResponseEnvelope404() {
        ResponseEnvelope result = responseEnvelopeConverter.convert(404);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getMessages().get(0).getMessage().contains("Not found"));
        Assert.assertEquals(((GenericResponseEnvelope) result).getMessages().stream().findFirst().get().getCode(),"404");
    }
}
