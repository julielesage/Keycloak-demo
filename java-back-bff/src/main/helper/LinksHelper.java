package com.yrnet.spark.sparkbxadmin.helper;

import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.QueryCriteria;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.ARG1;
import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.ARG2;
import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.FIRST;
import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.LAST;
import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.NEXT;
import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.PREVIOUS;
import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.SELF;
import static com.yrnet.spark.sparkbxadmin.constant.BxAdminConstant.URL_PAGE;

public class LinksHelper {

    private LinksHelper() {

    }

    public static Map<String, String> getLinks(Integer max, PaginatedQuery query, String pattern) {
        int lastPage = (int) Math.ceil(max / (double) query.getPageSize());
        String url = buildUrl(query, pattern);
        Map<String, String> links = new HashMap<>();

        links.put(SELF, url
                .replace(ARG1, String.valueOf(query.getPageNumber()))
                .replace(ARG2, String.valueOf(query.getPageSize())));

        if (lastPage > 1) {
            links.put(LAST, url
                    .replace(ARG1, String.valueOf(lastPage))
                    .replace(ARG2, String.valueOf(query.getPageSize())));

            if (query.getPageNumber() > 1) {
                links.put(FIRST, url
                        .replace(ARG1, "1")
                        .replace(ARG2, String.valueOf(query.getPageSize())));
                links.put(PREVIOUS, url
                        .replace(ARG1, String.valueOf(query.getPageNumber() - 1))
                        .replace(ARG2, String.valueOf(query.getPageSize())));
            }

            if (query.getPageNumber() < lastPage) {
                links.put(NEXT, url
                        .replace(ARG1, String.valueOf(query.getPageNumber() + 1))
                        .replace(ARG2, String.valueOf(query.getPageSize())));
            }

        }
        return links;
    }

    private static String buildUrl(PaginatedQuery query, String pattern) {
        StringBuilder url = new StringBuilder(pattern).append("?");
        if (CollectionUtils.isNotEmpty(query.getCriterias())) {
            for (QueryCriteria criteria : query.getCriterias()) {
                url.append(criteria.getField()).append("=").append(criteria.getQuery()).append("&");
            }
        }
        url.append(URL_PAGE);
        return url.toString();
    }

}
