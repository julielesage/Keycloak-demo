package com.yrnet.spark.sparkbxadmin.helper;

import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.QueryCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LinksHelperTest {

    private final String url = "/customers";
    private final List<QueryCriteria> criterias = Arrays.asList(new QueryCriteria("email", "toto@yopmail.com"));

    @Test
    public void get_links_one_result() {
        // MOCK
        final int result = 1;
        final int pageSize = 20;
        final int pageNumber = 1;
        final List<QueryCriteria> criterias = Arrays.asList(
                new QueryCriteria("realm", "YR-FR"),
                new QueryCriteria("email", "toto@yopmail.com"));
        final PaginatedQuery query = PaginatedQuery.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .criterias(criterias)
                .build();

        // WHEN
        final Map<String, String> links = LinksHelper.getLinks(result, query, url);

        // THEN
        assertThat(links.size()).isEqualTo(1);
        assertThat(links.get(SELF)).isEqualTo(url + "?realm=YR-FR&email=toto@yopmail.com&pageNumber="
                + pageNumber + "&pageSize=" + pageSize);
    }

    @Test
    public void get_links_two_pages_results_first_page() {
        // MOCK
        final int result = 30;
        final int pageSize = 20;
        final int pageNumber = 1;
        final PaginatedQuery query = PaginatedQuery.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .criterias(criterias)
                .build();

        // WHEN
        final Map<String, String> links = LinksHelper.getLinks(result, query, url);

        // THEN
        assertThat(links.size()).isEqualTo(3);
        assertThat(links.get(SELF)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber="
                + pageNumber + "&pageSize=" + pageSize);
        assertThat(links.get(LAST)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=2&pageSize=" + pageSize);
        assertThat(links.get(NEXT)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=2&pageSize=" + pageSize);
    }

    @Test
    public void get_links_five_pages_results_first_page() {
        // MOCK
        final int result = 45;
        final int pageSize = 10;
        final int pageNumber = 1;
        final PaginatedQuery query = PaginatedQuery.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .criterias(criterias)
                .build();

        // WHEN
        final Map<String, String> links = LinksHelper.getLinks(result, query, url);

        // THEN
        assertThat(links.size()).isEqualTo(3);
        assertThat(links.get(SELF)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber="
                + pageNumber + "&pageSize=" + pageSize);
        assertThat(links.get(LAST)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=5&pageSize=" + pageSize);
        assertThat(links.get(NEXT)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=2&pageSize=" + pageSize);
    }

    @Test
    public void get_links_five_pages_results_third_page() {
        // MOCK
        final int result = 45;
        final int pageSize = 10;
        final int pageNumber = 3;
        final PaginatedQuery query = PaginatedQuery.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .criterias(criterias)
                .build();

        // WHEN
        final Map<String, String> links = LinksHelper.getLinks(result, query, url);

        // THEN
        assertThat(links.size()).isEqualTo(5);
        assertThat(links.get(SELF)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber="
                + pageNumber + "&pageSize=" + pageSize);
        assertThat(links.get(LAST)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=5&pageSize=" + pageSize);
        assertThat(links.get(NEXT)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=4&pageSize=" + pageSize);
        assertThat(links.get(FIRST)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=1&pageSize=" + pageSize);
        assertThat(links.get(PREVIOUS)).isEqualTo(url + "?email=toto@yopmail.com&pageNumber=2&pageSize=" + pageSize);
    }

}
