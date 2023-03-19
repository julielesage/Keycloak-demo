package com.yrnet.spark.sparkbxadmin.api.loyalty;

import com.yrnet.spark.bxadmin.api.LoyaltyApi;
import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import com.yrnet.spark.bxadmin.model.Reward;
import com.yrnet.spark.loyaltyfacadeclient.dto.reward.RewardDTO;
import com.yrnet.spark.sparkbxadmin.api.response.LoyaltyResponseEnvelope;
import com.yrnet.spark.sparkbxadmin.helper.FeatureFlagHelper;
import com.yrnet.spark.sparkbxadmin.keycloak.KeycloakTokenHelper;
import com.yrnet.spark.sparkbxadmin.loyalty.LoyaltyFacade;
import com.yrnet.spark.sparkbxadmin.mapper.RewardDTOToRewardMapper;
import com.yrnet.spark.sparkbxadmin.mapper.RewardToRewardDTOMapper;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LoyaltyResource implements LoyaltyApi {

    private final RewardDTOToRewardMapper rewardDTOToRewardMapper;
    private final RewardToRewardDTOMapper rewardToRewardDTOMapper;
    private final LoyaltyFacade loyaltyFacade;
    private final FeatureFlagHelper featureFlagHelper;

    public LoyaltyResource(LoyaltyFacade loyaltyFacade,
                           RewardDTOToRewardMapper rewardDTOToRewardMapper,
                           RewardToRewardDTOMapper rewardToRewardDTOMapper,
                           FeatureFlagHelper featureFlagHelper) {
        this.loyaltyFacade = loyaltyFacade;
        this.rewardDTOToRewardMapper = rewardDTOToRewardMapper;
        this.rewardToRewardDTOMapper = rewardToRewardDTOMapper;
        this.featureFlagHelper = featureFlagHelper;
    }

    @Override
    public ResponseEntity<ResponseEnvelope> getAllRewardsByRealm(
            @ApiParam(value = "Realm ID", required = true) @PathVariable("realmId") String realmId) {


        if (!featureFlagHelper.isLoyaltyEnabled(realmId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (KeycloakTokenHelper.hasAccessForRole(realmId)) {
            List<RewardDTO> rewards = loyaltyFacade.getAllByRealm(realmId);
            if (CollectionUtils.isNotEmpty(rewards)) {
                ArrayList<Reward> collect = (ArrayList<Reward>) rewards.stream()
                        .map(rewardDTOToRewardMapper::rewardDTOToReward).collect(Collectors.toList());

                return Optional.of(collect)
                        .map(LoyaltyResponseEnvelope::new)
                        .map(r -> (ResponseEnvelope) r)
                        .map(r -> ResponseEntity.ok().body(r))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<ResponseEnvelope> createReward(
            @ApiParam(value = "Optional description in *Markdown*", required = true) @Valid @RequestBody Reward reward) {

        String context = reward != null ? reward.getContext() : null;

        if (!featureFlagHelper.isLoyaltyEnabled(context)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (KeycloakTokenHelper.hasAccessForRole(context)) {

            RewardDTO rewardDTO = rewardToRewardDTOMapper.rewardToRewardDTO(reward);

            final Optional<RewardDTO> createdReward = loyaltyFacade.create(rewardDTO);
            if (createdReward.isPresent()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<ResponseEnvelope> deleteReward(
            @ApiParam(value = "Reward ID", required = true) @PathVariable("id") String id) {


        if (loyaltyFacade.delete(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<ResponseEnvelope> updateReward(
            @ApiParam(value = "Optional description in *Markdown*", required = true) @Valid @RequestBody Reward reward) {

        String context = reward != null ? reward.getContext() : null;

        if (!featureFlagHelper.isLoyaltyEnabled(context)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (KeycloakTokenHelper.hasAccessForRole(context)) {

            RewardDTO rewardDTO = rewardToRewardDTOMapper.rewardToRewardDTO(reward);

            final Optional<RewardDTO> updateReward = loyaltyFacade.update(rewardDTO);
            if (updateReward.isPresent()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
