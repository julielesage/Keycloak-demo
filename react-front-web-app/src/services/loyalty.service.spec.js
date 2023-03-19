/**
 * @jest-environment jsdom
 */
process.env.NODE_ENV = "test";

import loyaltyService from 'services/loyaltyService';
import nock from "nock";
import handleServiceError from 'services/handleServiceError';
import API from "utils/API";

/* ------------------------------------------------------------------
  §§ MOCKS
------------------------------------------------------------------ */
jest.mock('utils/API');
const dispatch = jest.fn();
jest.mock('services/handleServiceError', () => jest.fn());

const base_url = process.env.REACT_APP_BX_ADMIN_URL;
const service = "/loyalty/reward";


const mockedResponse = {
	status: 200,
	data: {
		data: "infos",
	},
	error: null
}

const fakeAllLoyaltyData = [
	{
		"name": "WHAT'S LATER",
		"position": 5,
		"rewardType": "LATER",
	},
	{
		"name": "20OFF",
		"position": 5,
		"rewardType": "DISCOUNT",
	}
];

const fakeReward = {
	"uuid": "12231",
	"context": "YR-FR",
	code: "ertyu",
	"name": "fakeName",
	"position": 3,
	"rewardType": 'PRODUCT',
	"product": "erty",
	"discount": 6,
	"alternateCode": "20OFF",
	"labels": {
		"fr": "coucou"
	},
	"descriptions": {
		"fr": "coucou"
	},
	"startDate": String(new Date()),
	"endDate": String(new Date()),
	"active": false
}

/* ------------------------------------------------------------------
  §§ TESTS
------------------------------------------------------------------ */

describe("Loyalty service repository, calling api", () => {

	let fakeResponse;
	const nocking = nock(`${base_url}${service}`);

	// GET BY REALM
	it("should handle error if status is 401", async () => {
		nocking.get('/realm/YR-BE').reply(401);
		await loyaltyService.getByRealm('YR-BE', dispatch);
		expect(handleServiceError).toHaveBeenCalled();
	})
	it("should return 200 with data", async () => {
		// WHEN
		API.get = jest.fn(() => mockedResponse);
		// THEN
		fakeResponse = await loyaltyService.getByRealm('YR-FR', dispatch);
		// EXPECT
		expect(fakeResponse.status).toEqual(200);
		expect(fakeResponse.data).toEqual("infos");
	})

	// CREATE REWARD
	it("should handle error if status is 503", async () => {
		nocking.post('/create').reply(503);
		await loyaltyService.createReward(fakeReward, dispatch);
		expect(handleServiceError).toHaveBeenCalled();
	})
	it("should return 201 with new created reward", async () => {
		// WHEN
		API.post = jest.fn(() => mockedResponse);
		// THEN
		fakeResponse = await loyaltyService.createReward();
		// EXPECT
		expect(fakeResponse.status).toEqual(200);
		expect(fakeResponse.data).toEqual({ data: "infos" });
	})

	// UPDATE REWARD
	it("should handle error if status is 500", async () => {
		nocking.put('/update').reply(500);
		fakeResponse = await loyaltyService.updateReward(fakeReward, dispatch);
		expect(handleServiceError).toHaveBeenCalled();
	})
	it("should return 200 with data", async () => {
		// WHEN
		API.put = jest.fn(() => mockedResponse);
		// THEN
		fakeResponse = await loyaltyService.updateReward(fakeReward, dispatch);
		expect(fakeResponse.status).toEqual(200);
		expect(fakeResponse.data).toEqual({ data: "infos" });
	})

	// DELETE REWARD
	it("should handle error if status is 404", async () => {
		nocking.delete('/').reply(404);
		fakeResponse = await loyaltyService.deleteReward("");
		expect(handleServiceError).toHaveBeenCalled();
	})
	it("should return 200 if deleted", async () => {
		// WHEN
		API.delete = jest.fn(() => mockedResponse);
		// THEN
		fakeResponse = await loyaltyService.deleteReward("PRODUC", dispatch);
		expect(fakeResponse.status).toEqual(200);
	})
})