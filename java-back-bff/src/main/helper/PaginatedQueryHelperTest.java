package com.yrnet.spark.sparkbxadmin.helper;

import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.QueryCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class PaginatedQueryHelperTest {

    @InjectMocks
    private PaginatedQueryHelper paginatedQueryHelper;

    @Test
    public void hasCriteria_should_return_true_when_paginatedCrietria_contains_that_criteria() {
        // MOCK
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria loyaltyCriteria = new QueryCriteria();
        QueryCriteria realmCriteria = new QueryCriteria();
        loyaltyCriteria.setField("loyaltyCard");
        loyaltyCriteria.setQuery("12345678");
        realmCriteria.setField("realm");
        realmCriteria.setQuery("YR-FR");
        List<QueryCriteria> list = Arrays.asList(realmCriteria, loyaltyCriteria);
        paginatedQuery.setCriterias(list);
        // THEN
        assertThat(paginatedQueryHelper.hasCriteria(paginatedQuery, "loyaltyCard")).isTrue();
    }

    @Test
    public void hasCriteria_should_return_false_when_paginatedCriteria_do_not_contain_that_criteria() {
        // MOCK
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria loyaltyCriteria = new QueryCriteria();
        loyaltyCriteria.setField("loyaltyCard");
        loyaltyCriteria.setQuery("12345678");
        List<QueryCriteria> list = Arrays.asList(loyaltyCriteria);
        paginatedQuery.setCriterias(list);
        // THEN
        assertThat(paginatedQueryHelper.hasCriteria(paginatedQuery, "lastname")).isFalse();
    }

    @Test
    public void removeCriteria_should_remove_queryCriteria_from_criterias_list() {

        // MOCK
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria loyaltyCriteria = new QueryCriteria();
        loyaltyCriteria.setField("loyaltyCard");
        loyaltyCriteria.setQuery("12345678");
        List<QueryCriteria> list = new ArrayList<QueryCriteria>();
        list.add(loyaltyCriteria);
        paginatedQuery.setCriterias(list);

        // WHEN
        PaginatedQuery updatedQuery = paginatedQueryHelper.removeCriteria(paginatedQuery, "loyaltyCard");

        // THEN
        assertThat(updatedQuery.getCriterias().size()).isEqualTo(0);
    }

    @Test
    public void addCriteria() {

        // MOCK
        PaginatedQuery paginatedQuery = new PaginatedQuery();
        QueryCriteria loyaltyCriteria = new QueryCriteria();
        loyaltyCriteria.setField("loyaltyCard");
        loyaltyCriteria.setQuery("12345678");
        List<QueryCriteria> list = new ArrayList<QueryCriteria>();
        list.add(loyaltyCriteria);
        paginatedQuery.setCriterias(list);

        // WHEN
        PaginatedQuery updatedQuery = paginatedQueryHelper.removeCriteria(paginatedQuery, "loyaltyCard");

        // THEN
        assertThat(updatedQuery.getCriterias().size()).isEqualTo(0);
    }
}
