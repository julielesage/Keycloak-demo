package com.yrnet.spark.digitalaccount.api.functional.customer.v2;


import com.yrnet.spark.digitalaccount.api.functional.customer.guard.CustomerGuard;
import com.yrnet.spark.digitalaccount.api.technical.security.SparkUser;
import com.yrnet.spark.digitalaccount.api.technical.security.SparkUserRight;
import com.yrnet.spark.digitalaccount.api.technical.security.role.Role;
import com.yrnet.spark.digitalaccount.dto.CustomerDTO;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomerGuardTest {

    private CustomerGuard customerGuard = new CustomerGuard();

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void couldDelete_should_return_true_when_role_is_admin()
    {
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildAdminUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);


        boolean shoulddelete = customerGuard.couldDelete();


        Assert.assertTrue(shoulddelete);
    }

    @Test
    public void couldDelete_should_return_true_when_role_is_prod()
    {
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildProdUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);


        boolean shoulddelete = customerGuard.couldDelete();


        Assert.assertTrue(shoulddelete);
    }

    @Test
    public void couldDelete_should_return_true_when_role_is_dpo()
    {
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildDpodUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);


        boolean shoulddelete = customerGuard.couldDelete();


        Assert.assertTrue(shoulddelete);
    }

    @Test
    public void isReadAllowed_should_return_false_when_role_is_master()
    {
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildMasterUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);


        boolean isallowedToReadMaster = customerGuard.isReadAllowed(buildMasterUserRight().stream().findFirst().get());


        Assert.assertFalse(isallowedToReadMaster);
    }

    @Test
    public void isReadAllowed_should_return_true_when_other_role_then_master()
    {
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildAdminUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);


        boolean isallowedToReadMaster = customerGuard.isReadAllowed(buildAdminUserRight().stream().findFirst().get());


        Assert.assertTrue(isallowedToReadMaster);
    }


    @Test
   public void filterReadable_should_return_customer_when_user_is_allowed()
   {
       SparkUser sparkUser = buildSecurityContext();
       when(sparkUser.getSparkUserRights()).thenReturn(buildyrfrUserRight());
       when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);
       CustomerDTO customer=new CustomerDTO();
       customer.setRealmCode("YR-FR");


       Optional<CustomerDTO> customerResult = customerGuard.filterReadable(Optional.of(customer));


       Assert.assertTrue(Objects.nonNull(customerResult));

   }

    @Test(expected = AccessDeniedException.class)
    public void filterReadable_should_throw_exception_when_user_is_not_allowed()
    {
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildyrfrUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);
        CustomerDTO customer=new CustomerDTO();
        customer.setRealmCode("YR-DE");


        customerGuard.filterReadable(Optional.of(customer));

    }


    @Test(expected = AccessDeniedException.class)
    public void filterReadable_shouldthrow_exception_when_user_have_master_role()
    {
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildMasterUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);
        CustomerDTO customer=new CustomerDTO();
        customer.setRealmCode("YR-FR");


        customerGuard.filterReadable(Optional.of(customer));

    }

    @Test
    public void couldReadJobs_should_return_true_when_role_is_admin()
    {
        // MOCK
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildAdminUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);
        // WHEN
        boolean shouldReadJobs = customerGuard.couldReadJobs();
        // THEN
        Assert.assertTrue(shouldReadJobs);
    }

    @Test
    public void couldReadJobs_should_return_false_when_role_is_master()
    {
        // MOCK
        SparkUser sparkUser = buildSecurityContext();
        when(sparkUser.getSparkUserRights()).thenReturn(buildMasterUserRight());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(sparkUser);
        // WHEN
        boolean shouldReadJobs = customerGuard.couldReadJobs();
        // THEN
        Assert.assertFalse(shouldReadJobs);
    }

    private SparkUser buildSecurityContext() {
        SparkUser applicationUser = mock(SparkUser.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        return applicationUser;

    }



    private List<SparkUserRight> buildMasterUserRight()
    {
        SparkUserRight sparkUserRight= new SparkUserRight();
        sparkUserRight.setContext("ALL");
        sparkUserRight.setRole(Role.MASTER);
        sparkUserRight.setScope("GLOBAL");
        return Arrays.asList(sparkUserRight);
    }

    private List<SparkUserRight> buildAdminUserRight()
    {
        SparkUserRight sparkUserRight= new SparkUserRight();
        sparkUserRight.setContext("ALL");
        sparkUserRight.setRole(Role.ADMIN);
        sparkUserRight.setScope("GLOBAL");
        return Arrays.asList(sparkUserRight);
    }

    private List<SparkUserRight> buildProdUserRight()
    {
        SparkUserRight sparkUserRight= new SparkUserRight();
        sparkUserRight.setContext("ALL");
        sparkUserRight.setRole(Role.PROD);
        sparkUserRight.setScope("GLOBAL");
        return Arrays.asList(sparkUserRight);
    }
    private List<SparkUserRight> buildDpodUserRight()
    {
        SparkUserRight sparkUserRight= new SparkUserRight();
        sparkUserRight.setContext("ALL");
        sparkUserRight.setRole(Role.DPO);
        sparkUserRight.setScope("GLOBAL");
        return Arrays.asList(sparkUserRight);
    }
    private List<SparkUserRight> buildyrfrUserRight()
    {
        SparkUserRight sparkUserRight= new SparkUserRight();
        sparkUserRight.setContext("YR-FR");
        sparkUserRight.setRole(Role.MANAGER);
        sparkUserRight.setScope("SITE");
        return Arrays.asList(sparkUserRight);
    }
}
