package com.yrnet.spark.sparkbxadmin.token;


import com.yrnet.spark.sparkbxadmin.service.token.TokenService;
import io.jsonwebtoken.lang.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {

    @InjectMocks
    TokenService tokenService;

    @Test
    public void getaccesstokenShouldGenerateToken() {
        buildSecurityContext();
        String accessToken = tokenService.getAccessToken();
        Assert.notNull(accessToken);

    }

    @Test
    public void getaccesstokenShouldNotGenerateTokenWhenAnounymousUser() {
        buildAnounymousContext();
        String accessToken = tokenService.getAccessToken();
        Assert.isNull(accessToken);

    }

    private void buildAnounymousContext() {

        AnonymousAuthenticationToken authentication = mock(AnonymousAuthenticationToken.class);
        OidcKeycloakAccount account = mock(OidcKeycloakAccount.class);
        KeycloakSecurityContext securitycontext = mock(KeycloakSecurityContext.class);
        AccessToken token = new AccessToken();
        token.id("id");
        token.issuer("issuer");
        token.subject("subject");
        token.exp(100000L);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    private void buildSecurityContext() {
        KeycloakAuthenticationToken authentication = mock(KeycloakAuthenticationToken.class);
        OidcKeycloakAccount account = mock(OidcKeycloakAccount.class);
        KeycloakSecurityContext securitycontext = mock(KeycloakSecurityContext.class);
        AccessToken token = new AccessToken();
        token.id("id");
        token.issuer("issuer");
        token.subject("subject");
        token.exp(100000L);

        when(securitycontext.getToken()).thenReturn(token);
        when(account.getKeycloakSecurityContext()).thenReturn(securitycontext);
        Mockito.when(authentication.getAccount()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }
}
