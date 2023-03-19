package com.yrnet.spark.sparkbxadmin.api.customer;

import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerDTO;
import com.yrnet.spark.loyaltyfacadeclient.dto.LoyaltyCustomerDTO;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.QueryCriteria;
import com.yrnet.spark.sparkbxadmin.helper.PaginatedQueryHelper;
import com.yrnet.spark.sparkbxadmin.service.customer.CustomerService;
import com.yrnet.spark.sparkbxadmin.loyalty.LoyaltyFacade;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CustomerFacade {
    private final CustomerService customerService;
    private final LoyaltyFacade loyaltyFacade;


    public CustomerFacade(CustomerService customerService,
                          LoyaltyFacade loyaltyFacade) {
        this.customerService = customerService;
        this.loyaltyFacade = loyaltyFacade;
    }

    /**
     * @param uid "12345DCLHI"
     * @return customerDTO
     */
    public CustomerDTO getCustomer(String uid) {
        return customerService.getCustomer(uid);
    }

    /**
     * @param paginatedQuery {fullText = "", whereClose = "", criterias = {..., ...}, pageNumber = 0, pageSize = 20}
     * @return ResponseEnvelope containing list of customerDTO corresponding to the research or empty
     */
    public Optional<ResponseEnvelope> getCustomers(PaginatedQuery paginatedQuery) {

        String realm = paginatedQuery.getCriterias().stream()
                .filter(criteria -> "realm".equals(criteria.getField()))
                .findFirst().map(QueryCriteria::getQuery)
                .orElse(Strings.EMPTY);

        Optional<ResponseEnvelope> result = customerService.getCustomers(paginatedQuery);

        if (!result.isPresent() && PaginatedQueryHelper.hasCriteria(paginatedQuery, "loyaltyCard")
            && loyaltyFacade.isActive(realm)) {
            return getCustomersByLoyaltyFacade(paginatedQuery);
        }
        return result;
    }

    /**
     *
     * @param paginatedQuery {fullText = "", whereClose = "", criterias = {loyaltyCard = "1234567"}, ...}
     * @return response with customer list from digacc
     */
    public Optional<ResponseEnvelope> getCustomersByLoyaltyFacade(PaginatedQuery paginatedQuery) {
             Optional<LoyaltyCustomerDTO> loyaltyCustomer = loyaltyFacade.getLoyaltyCustomerByCard(paginatedQuery);

             if (loyaltyCustomer.isPresent() && isCompatibleWithCsidQuery(loyaltyCustomer.get(), paginatedQuery)) {
                 PaginatedQuery removedPaginatedQuery = PaginatedQueryHelper.removeCriteria(paginatedQuery, "loyaltyCard");
                 PaginatedQuery updatedPaginatedQuery = PaginatedQueryHelper
                         .addCriteriaWithStringValue(removedPaginatedQuery, "csid", loyaltyCustomer.get().getCsid());
                 return getCustomers(updatedPaginatedQuery);
             }
        return Optional.empty();
    }

    /**
     *
     * @param loyaltyCustomerDTO {firstname = "", lastname = "", csid = "" , email = "", loyalty = {...}}
     * @param paginatedQuery {fullText = "", whereClose = "", criterias = {..., ...}, pageNumber = 0, pageSize = 20}
     * @return true if possible to call digacc with loyaltyCustomer csid
     */
    public boolean isCompatibleWithCsidQuery(LoyaltyCustomerDTO loyaltyCustomerDTO, PaginatedQuery paginatedQuery) {
        if (!PaginatedQueryHelper.hasCriteria(paginatedQuery, "csid")) {
            return true;
        }
        String csidQuery = paginatedQuery.getCriterias().stream()
                .filter(criteria -> "csid".equals(criteria.getField()))
                .findFirst().map(QueryCriteria::getQuery)
                .orElse(Strings.EMPTY);
        return csidQuery.equals(loyaltyCustomerDTO.getCsid());
    }

}
