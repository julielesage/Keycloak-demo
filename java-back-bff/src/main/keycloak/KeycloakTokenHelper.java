package com.yrnet.spark.sparkbxadmin.keycloak;

import com.yrnet.spark.sparkbxadmin.service.token.DataRole;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class KeycloakTokenHelper {

    private static final List<String> ROLES = new ArrayList<>(Arrays.asList("MASTER", "MANAGER", "PROD", "ADMIN"));
    private static final int AUTHORITY_LENGTH = 5;
    private static final String UNDERSCORE = "_";

    private KeycloakTokenHelper() {
    }

    /**
     * @param authorities granted authorities {"ROLE_BRAND_YR_MANAGER", "uma_authorization"}
     * @return list of dataRole for Spark-Admin {{scope : "BRAND", context : "YR", role : "MANAGER}}
     */
    public static List<DataRole> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(e -> e.length() > AUTHORITY_LENGTH)
                .map(e -> e.substring(AUTHORITY_LENGTH))
                .map(KeycloakTokenHelper::getDataRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * @param role string "ROLE_BRAND_YR_MANAGER"
     * @return datarole {scope :"BRAND", context:"YR", role:"MANAGER"}
     */
    private static DataRole getDataRole(String role) {
        final String[] authors = role.split(UNDERSCORE);
        if (authors.length != 3) {
            return null;
        }
        return DataRole.builder().scope(authors[0]).context(authors[1]).role(authors[2]).build();
    }


    public static boolean hasAccessForRole(String realmId) {

        List<DataRole> roles = getDataRoles();

        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }

        for (DataRole role : roles) {
            if (((role.getScope().equals("BRAND") && role.getContext().equals("YR"))
                    || (role.getScope().equals("GLOBAL") && role.getContext().equals("ALL"))
                    || role.getContext().equals(realmId))
                    && (ROLES.contains(role.getRole()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotAuthorized() {

        List<DataRole> roles = getDataRoles();

        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }

        for (DataRole role : roles) {
            if (role.getRole().equalsIgnoreCase("READONLY")||role.getRole().equalsIgnoreCase("CSUPPORT") || role.getRole().equalsIgnoreCase("MANAGER")|| role.getRole().equalsIgnoreCase("MASTER")) {
                return true;
            }
        }
        return false;
    }

    private static List<DataRole> getDataRoles() {
        return mapAuthorities(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

}
