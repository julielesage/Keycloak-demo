package com.yrnet.spark.sparkbxadmin.keycloak;

import com.yrnet.spark.sparkbxadmin.service.token.DataRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class KeycloakTokenHelperTest {

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER", "uma_autorization", "ROLE_GLOBAL_ALL_ADMIN"})
    public void mapAuthorities_should_return_filtered_list_of_datarole() {

        // WHEN
        final List<DataRole> userDataRoleList = KeycloakTokenHelper.mapAuthorities(SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities());


        // THEN
        assertThat(userDataRoleList.get(0).getRole()).isEqualTo("MANAGER");
        assertThat(userDataRoleList.get(0).getContext()).isEqualTo("YR");
        assertThat(userDataRoleList.get(0).getScope()).isEqualTo("BRAND");
        assertThat(userDataRoleList.get(1).getRole()).isEqualTo("ADMIN");
        assertThat(userDataRoleList.get(1).getContext()).isEqualTo("ALL");
        assertThat(userDataRoleList.get(1).getScope()).isEqualTo("GLOBAL");
        assertThat(userDataRoleList.size()).isEqualTo(2);

    }

}
