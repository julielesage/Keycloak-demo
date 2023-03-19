
import { loyaltyRewards } from '../../../store/reducers/loyalty.reducer';
import initialState from "../../../store/initialStates/loyalty.initialState";

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

describe("loyalty reducer", () => {
  it("place rewards into store", () => {
    expect(loyaltyRewards(initialState, { type: 'GET_REWARDS', payload: fakeAllLoyaltyData })).toEqual(fakeAllLoyaltyData);
  });
  it("should do nothing, but return the state", () => {
    expect(loyaltyRewards(initialState, { type: "" })).toEqual(initialState);
  })
})