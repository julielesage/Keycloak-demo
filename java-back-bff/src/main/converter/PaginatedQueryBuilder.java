package com.yrnet.spark.sparkbxadmin.converter;

import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.QueryCriteria;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PaginatedQueryBuilder {

    public static final String REALM = "realm";
    public static final String EMAIL = "email";
    public static final String REGISTRATION_EMAIL = "registrationEmail";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String PSEUDO = "pseudo";
    public static final String UID = "uid";
    public static final String CSID = "csid";
    public static final String LOYALTY_CARD = "loyaltyCard";
    public static final String ID_VAD = "idVAD";

    public PaginatedQuery init(String pageSize, String pageNumber) {
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        paginatedQuery.setCriterias(new ArrayList<>());

        if (StringUtils.isNotBlank(pageSize)) {
            paginatedQuery.setPageSize(Integer.parseInt(pageSize));
        }
        if (StringUtils.isNotBlank(pageNumber)) {
            paginatedQuery.setPageNumber(Integer.parseInt(pageNumber));
        }

        return paginatedQuery;
    }

    public void addRealm(PaginatedQuery paginatedQuery, String realm) {
        if (StringUtils.isNotBlank(realm)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(REALM).query(realm).build());
        }
    }

    public void addEmail(PaginatedQuery paginatedQuery, String email) {
        if (StringUtils.isNotBlank(email)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(EMAIL).query(email).build());
        }
    }

    public void addRegistrationEmail(PaginatedQuery paginatedQuery, String registrationEmail) {
        if (StringUtils.isNotBlank(registrationEmail)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(REGISTRATION_EMAIL).query(registrationEmail).build());
        }
    }

    public void addFirstName(PaginatedQuery paginatedQuery, String firstName) {
        if (StringUtils.isNotBlank(firstName)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(FIRSTNAME).query(firstName).build());
        }
    }

    public void addLastName(PaginatedQuery paginatedQuery, String lastName) {
        if (StringUtils.isNotBlank(lastName)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(LASTNAME).query(lastName).build());
        }
    }

    public void addPseudo(PaginatedQuery paginatedQuery, String pseudo) {
        if (StringUtils.isNotBlank(pseudo)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(PSEUDO).query(pseudo).build());
        }
    }

    public void addUid(PaginatedQuery paginatedQuery, String uid) {
        if (StringUtils.isNotBlank(uid)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(UID).query(uid).build());
        }
    }

    public void addCsid(PaginatedQuery paginatedQuery, String cisd) {
        if (StringUtils.isNotBlank(cisd)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(CSID).query(cisd).build());
        }
    }

    public void addLoyaltyCard(PaginatedQuery paginatedQuery, String loyaltyCard) {
        if (StringUtils.isNotBlank(loyaltyCard)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(LOYALTY_CARD).query(loyaltyCard).build());
        }
    }

    public void addIdVad(PaginatedQuery paginatedQuery, String idVad) {
        if (StringUtils.isNotBlank(idVad)) {
            paginatedQuery.getCriterias().add(QueryCriteria.builder().field(ID_VAD).query(idVad).build());
        }
    }

}
