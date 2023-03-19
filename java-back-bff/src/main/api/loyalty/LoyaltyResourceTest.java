package com.yrnet.spark.sparkbxadmin.api.loyalty;

import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.bxadmin.model.Reward;
import com.yrnet.spark.loyaltyfacadeclient.dto.reward.RewardDTO;
import com.yrnet.spark.sparkbxadmin.helper.FeatureFlagHelper;
import com.yrnet.spark.sparkbxadmin.loyalty.LoyaltyFacade;
import com.yrnet.spark.sparkbxadmin.mapper.RewardDTOToRewardMapper;
import com.yrnet.spark.sparkbxadmin.mapper.RewardToRewardDTOMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class LoyaltyResourceTest {

    @Mock
    private RewardDTOToRewardMapper rewardDTOToRewardMapper;
    @Mock
    private RewardToRewardDTOMapper rewardToRewardDTOMapper;
    @Mock
    private LoyaltyFacade loyaltyFacade;
    @Mock
    private FeatureFlagHelper featureFlagHelper;
    @InjectMocks
    private LoyaltyResource loyaltyResource;


    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void getAllRewardsByRealm_with_loyalty_enabled_and_authorized_should_return_ok() {

        String realm = "realm";

        List<RewardDTO> rewards = buildRewards();

        when(featureFlagHelper.isLoyaltyEnabled(realm)).thenReturn(true);
        when(loyaltyFacade.getAllByRealm(realm)).thenReturn(rewards);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.getAllRewardsByRealm(realm);

        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    public void getAllRewardsByRealm_with_loyalty_disabled_should_return_forbidden() {

        String realm = "realm";

        when(featureFlagHelper.isLoyaltyEnabled(realm)).thenReturn(false);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.getAllRewardsByRealm(realm);

        Assert.assertEquals(HttpStatus.FORBIDDEN, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"not_authorized_user"})
    public void getAllRewardsByRealm_with_loyalty_enabled_and_unauthorized_should_return_forbidden() {

        String realm = "realm";

        when(featureFlagHelper.isLoyaltyEnabled(realm)).thenReturn(true);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.getAllRewardsByRealm(realm);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void getAllRewardsByRealm_with_loyalty_enabled_and_authorized_should_return_no_content() {

        String realm = "realm";

        when(featureFlagHelper.isLoyaltyEnabled(realm)).thenReturn(true);
        when(loyaltyFacade.getAllByRealm(realm)).thenReturn(null);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.getAllRewardsByRealm(realm);

        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEnvelopeResponseEntity.getStatusCode());
    }

    private List<RewardDTO> buildRewards() {
        return Arrays.asList(new RewardDTO(), new RewardDTO(), new RewardDTO());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void createReward_should_return_ok() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(true);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.create(rewardDTO)).thenReturn(Optional.of(rewardDTO));

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.createReward(reward);

        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"not_authorized_user"})
    public void createReward_with_unauthorized_user_should_return_unauthorized() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(true);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.create(rewardDTO)).thenReturn(Optional.of(rewardDTO));

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.createReward(reward);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEnvelopeResponseEntity.getStatusCode());

    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void createReward_with_loyalty_disabled_should_return_forbidden() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(false);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.create(rewardDTO)).thenReturn(Optional.of(rewardDTO));

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.createReward(reward);

        Assert.assertEquals(HttpStatus.FORBIDDEN, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void createReward_with_null_reward_dto_should_return_not_found() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(true);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.create(rewardDTO)).thenReturn(Optional.empty());

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.createReward(reward);

        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void createReward_with_null_reward_should_return_forbidden() {
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.createReward(null);

        Assert.assertEquals(HttpStatus.FORBIDDEN, responseEnvelopeResponseEntity.getStatusCode());
    }


    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void deleteReward_should_return_ok(){

        String id = "id";
        when(loyaltyFacade.delete(id)).thenReturn(true);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.deleteReward(id);

        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void deleteReward_should_return_not_found(){

        String id = "id";
        when(loyaltyFacade.delete(id)).thenReturn(false);

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.deleteReward(id);

        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEnvelopeResponseEntity.getStatusCode());

    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void updateReward_should_return_ok() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(true);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.update(rewardDTO)).thenReturn(Optional.of(rewardDTO));

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.updateReward(reward);

        Assert.assertEquals(HttpStatus.OK, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"not_authorized_user"})
    public void updateReward_with_unauthorized_user_should_return_unauthorized() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(true);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.update(rewardDTO)).thenReturn(Optional.of(rewardDTO));

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.updateReward(reward);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEnvelopeResponseEntity.getStatusCode());

    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void updateReward_with_loyalty_disabled_should_return_forbidden() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(false);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.update(rewardDTO)).thenReturn(Optional.of(rewardDTO));

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.updateReward(reward);

        Assert.assertEquals(HttpStatus.FORBIDDEN, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void updateReward_with_null_reward_dto_should_return_not_found() {
        String context = "realm";

        RewardDTO rewardDTO = new RewardDTO();
        Reward reward = new Reward();
        reward.setContext(context);

        when(featureFlagHelper.isLoyaltyEnabled(reward.getContext())).thenReturn(true);
        when(rewardToRewardDTOMapper.rewardToRewardDTO(reward)).thenReturn(rewardDTO);
        when(loyaltyFacade.update(rewardDTO)).thenReturn(Optional.empty());

        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.updateReward(reward);

        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEnvelopeResponseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(username = "toto", authorities = {"ROLE_BRAND_YR_MANAGER"})
    public void updateReward_with_null_reward_should_return_forbidden() {
        ResponseEntity<ResponseEnvelope> responseEnvelopeResponseEntity = loyaltyResource.updateReward(null);

        Assert.assertEquals(HttpStatus.FORBIDDEN, responseEnvelopeResponseEntity.getStatusCode());
    }


}
