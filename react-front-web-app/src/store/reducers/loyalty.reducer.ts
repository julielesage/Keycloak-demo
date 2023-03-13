import initialStateReward from 'store/initialStates/loyalty.initialState';
import { Action } from "utils/types";

export const loyaltyRewards = (state = initialStateReward, action: Action) => {
	if (action.type === "GET_REWARDS")
		return action.payload;
	else
		return state;
};
