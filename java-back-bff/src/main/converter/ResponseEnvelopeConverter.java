package com.yrnet.spark.sparkbxadmin.converter;

import com.yrnet.spark.bxadmin.model.CustomerResult;
import com.yrnet.spark.bxadmin.model.DataCustomerResult;
import com.yrnet.spark.bxadmin.model.Message;
import com.yrnet.spark.bxadmin.model.Pagination;
import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.digitalaccountclient.dto.customer.SearchResultCustomerDTO;
import com.yrnet.spark.sparkbxadmin.api.response.GenericResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.helper.LinksHelper;
import com.yrnet.spark.sparkbxadmin.mapper.CustomerDTOToCustomerResultMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResponseEnvelopeConverter {


    private final CustomerDTOToCustomerResultMapper customerDTOToCustomerResultMapper;

    public ResponseEnvelopeConverter(CustomerDTOToCustomerResultMapper customerDTOToCustomerResultMapper) {
        this.customerDTOToCustomerResultMapper = customerDTOToCustomerResultMapper;
    }

    public ResponseEnvelope convert(SearchResultCustomerDTO dto, PaginatedQuery query) {
        final List<CustomerResult> customerResult = dto.getCustomers()
                .stream()
                .map(customerDTOToCustomerResultMapper::customerDTOToCustomerResult)
                .collect(Collectors.toList());

        final DataCustomerResult dataCustomerResult = new DataCustomerResult();
        dataCustomerResult.setResults(customerResult);
        dataCustomerResult.setPagination(getPagination(dto, query));

        final GenericResponseEnvelope response = new GenericResponseEnvelope(dataCustomerResult);
        response.addCallId();
        response.addMessagesItem(getMessage(HttpStatus.OK.value()));

        return response;
    }

    public ResponseEnvelope convert(int status) {
        final GenericResponseEnvelope response = new GenericResponseEnvelope(null);
        response.addCallId();
        response.addMessagesItem(getMessage(status));
        return response;
    }

    private Pagination getPagination(SearchResultCustomerDTO dto, PaginatedQuery query) {
        final Pagination pagination = new Pagination();
        pagination.setTotalCount(dto.getMaxResult());
        pagination.setPage(query.getPageNumber());
        pagination.setPerPage(query.getPageSize());
        pagination.setPageCount(dto.getCustomers().size());
        pagination.setLinks(LinksHelper.getLinks(dto.getMaxResult(), query, "/customer"));
        return pagination;
    }

    private Message getMessage(int status) {
        Message message = new Message();
        message.setCode(String.valueOf(status));
        if (status == HttpStatus.FORBIDDEN.value()) {
            message.setMessage("You don't have right to perform this action. Please contact sparkprd@yrnet.com to get the right access rights");
        } else if (status != HttpStatus.OK.value()) {
            message.setMessage("Not found");
        }
        return message;
    }


}
