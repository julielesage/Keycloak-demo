package com.yrnet.spark.sparkbxadmin.api.response;

import com.yrnet.spark.bxadmin.model.Reward;

import java.util.ArrayList;

public class LoyaltyResponseEnvelope extends GenericResponseEnvelope<ArrayList<Reward>> {

    public LoyaltyResponseEnvelope(ArrayList<Reward> data) {
        super(data);
    }

}
