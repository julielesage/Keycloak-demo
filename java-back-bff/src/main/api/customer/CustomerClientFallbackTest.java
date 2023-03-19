package com.yrnet.spark.sparkbxadmin.api.customer;

import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerDTO;
import com.yrnet.spark.digitalaccountclient.dto.customer.CustomerOrderHistory;
import com.yrnet.spark.digitalaccountclient.dto.customer.SearchResultCustomerDTO;
import com.yrnet.spark.sparkbxadmin.auditclient.data.domain.PaginatedQuery;
import com.yrnet.spark.sparkbxadmin.digaccclient.dto.SocialNetworkUserAssociations;
import com.yrnet.spark.sparkbxadmin.digaccclient.fallback.CustomerClientFallback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class CustomerClientFallbackTest {

    @InjectMocks
    CustomerClientFallback customerClientFallback;

    @Mock
    private  Throwable cause;

    @Test
    public void getCustomerTest()
    {
        CustomerDTO uid = customerClientFallback.getCustomer("uid");
        Assert.assertNull(uid);
    }

    @Test
    public void getCustomerByEmailAndRealmTest()
    {
        CustomerDTO uid = customerClientFallback.getCustomerByEmailAndRealm("test@test.com","YR-FR");
        Assert.assertNull(uid);
    }

    @Test
    public void getCustomerOrderHistoryByCustomerUidTest()
    {
        CustomerOrderHistory uid = customerClientFallback.getCustomerOrderHistoryByCustomerUid("uid");
        Assert.assertNull(uid);
    }

    @Test
    public void getCustomersTest()
    {
        PaginatedQuery object= new PaginatedQuery();
        SearchResultCustomerDTO customers = customerClientFallback.getCustomers((object));
        Assert.assertNotNull(customers);
        Assert.assertEquals(customers.getMaxResult(),Integer.valueOf(0));
    }

    @Test
   public void getSocialAssociationByCustomerUidTest()
   {
       SocialNetworkUserAssociations socialAssociationByCustomerUid = customerClientFallback.getSocialAssociationByCustomerUid("uid");
       Assert.assertNull(socialAssociationByCustomerUid);
   }
}
