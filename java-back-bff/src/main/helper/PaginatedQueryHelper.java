package com.yrnet.spark.sparkbxadmin.helper;

import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.QueryCriteria;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public final class PaginatedQueryHelper {

    private PaginatedQueryHelper() {

    }

    /**
     * @param paginatedQuery with a list of search criterias
     * @paraml criteriaField = "loyaltyCard"
     * @return true if contain that criteria
     */
    public static boolean hasCriteria(PaginatedQuery paginatedQuery, String criteriaField) {
        return paginatedQuery.getCriterias().stream().anyMatch(c -> criteriaField.equals(c.getField()));
    }

    /**
     * @param paginatedQuery with a list of search criterias
     * @param fieldName "lastname"
     * @return paginatedQuery = {criterias = list with lastname criteria}
     */
    public static PaginatedQuery removeCriteria(PaginatedQuery paginatedQuery, String fieldName) {
        if (StringUtils.isNotBlank(fieldName)) {
            List<QueryCriteria> newList = paginatedQuery.getCriterias();
            newList.removeIf(c -> fieldName.equals(c.getField()));
            paginatedQuery.setCriterias(newList);
        }
        return paginatedQuery;
    }

    /**
     * @param paginatedQuery with a list of search criterias
     * @param fieldName "lastname"
     * @return
     */
    public static PaginatedQuery addCriteriaWithStringValue(PaginatedQuery paginatedQuery, String fieldName, String value) {
        if (StringUtils.isNotBlank(fieldName)) {
            QueryCriteria query = new QueryCriteria();
            query.setField(fieldName);
            query.setQuery(value);
            List<QueryCriteria> newList = paginatedQuery.getCriterias();
            newList.add(query);
            paginatedQuery.setCriterias(newList);
        }
        return paginatedQuery;
    }
}
