package com.yrnet.spark.digitalaccount.api.functional.customer.guard;

import com.yrnet.spark.digitalaccount.api.functional.customer.domain.Administration;
import com.yrnet.spark.digitalaccount.api.functional.customer.domain.Brand;
import com.yrnet.spark.digitalaccount.api.technical.security.SparkUser;
import com.yrnet.spark.digitalaccount.api.technical.security.SparkUserRight;
import com.yrnet.spark.digitalaccount.api.technical.security.role.Role;
import com.yrnet.spark.digitalaccount.dto.CustomerDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.yrnet.spark.digitalaccount.core.jobs.constant.JobConstant.COULD_READ_JOBS;

@Slf4j
@Component
public class CustomerGuard {
    /**
     * @return Returns internal user
     */
    private SparkUser getSparkUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SparkUser) {
            return (SparkUser) principal;
        }
        throw new AccessDeniedException("User must sparkuser type to get roles");

    }

    /**
     * Does right allo to read?
     */
    public boolean isReadAllowed(SparkUserRight right) {
        return right != null && !"MASTER".equals(right.getRole().name()); // All roles could reads customer except master
    }

    /**
     * @return Returns true if given user & right could read this customer
     */
    private boolean couldRead(SparkUser user, SparkUserRight right, CustomerDTO customer) {
        switch (right.getScope()) {
            case "CUSTOMER":
                return customer.getUid().equals(user.getUsername());
            case "SITE":
                return isReadAllowed(right) && right.getContext().equals(customer.getRealmCode());
            case "BRAND":
                return isReadAllowed(right) && Brand.listSites(right.getContext()).contains(customer.getRealmCode());
            case "GLOBAL":
                return isReadAllowed(right);
        }
        return false;
    }

    /**
     * Returns the customer in input if readable else throws AccessDeniedException
     */
    public Optional<CustomerDTO> filterReadable(Optional<CustomerDTO> customer) {
        if (customer.isPresent()) {
            SparkUser user = getSparkUser();
            // Check if at least on role allow reading
            for (SparkUserRight right : user.getSparkUserRights()) {
                if (couldRead(user, right, customer.get())) {
                    return customer; // read allowed
                }
            }
            throw new AccessDeniedException("No role allowed to read this customer");
        }

        return Optional.empty();
    }

    /**
     * Calculate all possible readable realm for the user. If none searchable throws AccessDeniedException.
     */
    public List<String> calculateSearchableRealm() {
        SparkUser user = getSparkUser();
        List<String> searchableRealm = new LinkedList<>();
        for (SparkUserRight right : user.getSparkUserRights()) {
            if (isReadAllowed(right)) {
                switch (right.getScope()) {
                    case "CUSTOMER":
                        break; // Customer could not search other customers (except if social selling but specific scope required)
                    case "SITE":
                        searchableRealm.add(right.getContext());
                        break;
                    case "BRAND":
                        searchableRealm.addAll(Brand.listSites(right.getContext()));
                        break;
                    case "GLOBAL":
                        searchableRealm.addAll(Administration.listAllSites());
                        break;
                }
            }
        }
        if (searchableRealm.isEmpty()) {
            throw new AccessDeniedException("No searchable realm");
        }
        return searchableRealm;
    }

    /**
     * Calculate searchable realm for the user and limited to a specific list. If none searchable throws AccessDeniedException
     */
    public List<String> calculateSearchableRealm(List<String> realms) throws AccessDeniedException {
        if (CollectionUtils.isEmpty(realms)) { // same effect  as calling without realms
            return calculateSearchableRealm();
        }
        List<String> allowedRealm = calculateSearchableRealm();
        List<String> searchableRealm = new LinkedList<>();
        for (String realm : realms) { // Checks realms are in allowed realm
            if (!allowedRealm.contains(realm)) { // realm not accessible
                log.error("This realm is not accessible for this user " + realm);
                continue;
            }
            searchableRealm.add(realm);
        }
        if (searchableRealm.isEmpty()) {
            throw new AccessDeniedException("No searchable realms found");
        }
        return searchableRealm;
    }

    public boolean couldDelete() {
        SparkUser user = getSparkUser();
        for (SparkUserRight right : user.getSparkUserRights()) {
            return "ADMIN".equals(right.getRole().name()) || "PROD".equals(right.getRole().name()) || "DPO".equals(right.getRole().name());
        }
        throw new AccessDeniedException("No role allowed to delete customer");
    }

    /**
     *
     * @return boolean if allowed or not
     */
    public boolean couldReadJobs() {
        return getSparkUser().getSparkUserRights().stream()
            .map(SparkUserRight::getRole)
            .map(Role::name)
            .anyMatch(COULD_READ_JOBS::contains);
    }

}
