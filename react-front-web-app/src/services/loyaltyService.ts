import { store } from "store";
import { RewardType } from "store/initialStates/loyalty.initialState";
import API from "./API";
import handleServiceError from "./handleServiceError";

const service = "/loyalty/reward";

const getByRealm = async (realmCode: string, dispatch: typeof store.dispatch) => {
	try {
		const response = await API.get(
			`${service}/realm/${realmCode}`,
			{ timeout: 5000 }
		);
		return { status: response.status, data: response.data.data };
	} catch (error) {
		handleServiceError(error, dispatch);
	}
}

const updateReward = async (reward: RewardType, dispatch: typeof store.dispatch) => {
	try {
		const response = await API.put(
			`${service}`,
			JSON.stringify(reward),
			{ timeout: 5000 }
		);
		return { status: response.status, data: response.data };
	} catch (error) {
		handleServiceError(error, dispatch);
	}
};

const createReward = async (reward: RewardType, dispatch: typeof store.dispatch) => {
	try {
		const response = await API.post(
			`${service}`,
			JSON.stringify(reward),
			{ timeout: 5000 }
		);
		return { status: response.status, data: response.data };
	} catch (error) {
		handleServiceError(error, dispatch);
	}
};

const deleteReward = async (uuid: string, dispatch: typeof store.dispatch) => {
	try {
		const response = await API.delete(
			`${service}/${uuid}`,
			{ timeout: 5000 }
		);
		return { status: response.status, data: response.data };
	} catch (error) {
		handleServiceError(error, dispatch);
	}
};

const loyaltyService = {
	getByRealm,
	createReward,
	updateReward,
	deleteReward
};

export default loyaltyService;
