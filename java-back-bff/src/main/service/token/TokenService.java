package com.yrnet.spark.sparkbxadmin.service.token;

import com.yrnet.spark.sparkbxadmin.helper.TokenHelper;
import com.yrnet.spark.sparkbxadmin.keycloak.KeycloakTokenHelper;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private static final String RESOURCE_ID = "VPI";

    public String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            final AccessToken keyCloakAccessToken = ((KeycloakAuthenticationToken) authentication).getAccount().getKeycloakSecurityContext().getToken();

            return TokenHelper.createJWT(keyCloakAccessToken.getId(),
                    keyCloakAccessToken.getIssuer(),
                    keyCloakAccessToken.getSubject(),
                    keyCloakAccessToken.getExp(),
                    claimJwtCustomer(authentication, keyCloakAccessToken));
        }
        return null;
    }


    private Map<String, Object> claimJwtCustomer(Authentication authentication, AccessToken keyCloakAccessToken) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("aud", RESOURCE_ID);
        map.put("client_id", "bx-admin");
        map.put("given_name", keyCloakAccessToken.getGivenName());
        map.put("family_name", keyCloakAccessToken.getFamilyName());
        map.put("middle_name", keyCloakAccessToken.getMiddleName());
        map.put("email", keyCloakAccessToken.getEmail());
        map.put("phone_number", keyCloakAccessToken.getPhoneNumber());
        map.put("email_verified", keyCloakAccessToken.getEmailVerified());
        map.put("phone_verified", keyCloakAccessToken.getPhoneNumberVerified());
        map.put("scope", KeycloakTokenHelper.mapAuthorities(authentication.getAuthorities()).stream().map(DataRole::getScope).collect(Collectors.toList()));
        map.put("authorities", KeycloakTokenHelper.mapAuthorities(authentication.getAuthorities()).stream().map(DataRole::getRole).collect(Collectors.toList()));
        map.put("gr_role", KeycloakTokenHelper.mapAuthorities(authentication.getAuthorities()));
        return map;
    }


}
