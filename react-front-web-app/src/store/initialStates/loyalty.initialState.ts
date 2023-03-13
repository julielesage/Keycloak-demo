const initialStateReward: LoyaltyReqType | null = null;

export interface LoyaltyReqType {
	products?: Array<RewardType> | null,
	discounts?: Array<RewardType> | null,
	treeplanting?: Array<RewardType> | null,
	later?: Array<RewardType> | null
}
export interface RewardLineType {
	reward: RewardType,
	gettingOut?: () => void | undefined
}
export interface RewardType {
	uuid: string | undefined,
	context: string,
	code: string,
	externalId: string,
	name: string,
	position: number,
	rewardType: string | undefined,
	product: string | undefined,
	discount: number,
	alternateCode: string | undefined,
	labels: any | undefined,
	descriptions: any | undefined,
	startDate: Date,
	endDate: Date,
	active: boolean
}

export const emptyReward: RewardType = {
	"uuid": undefined,
	"context": "YR-FR",
	"code": "enter code",
	"externalId": "enter external ID",
	"name": "enter reward name",
	"position": 0,
	"rewardType": undefined,
	"product": "enter product reference",
	"discount": 0,
	"alternateCode": "enter alternate code",
	"labels": {
		"fr": undefined
	},
	"descriptions": {
		"fr": undefined
	},
	"startDate": new Date(),
	"endDate": new Date(),
	"active": false
}

export default initialStateReward;