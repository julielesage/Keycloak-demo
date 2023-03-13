import userService from "services/userService";

export const realmCodes = [
	{ value: "YR-AT", label: "YR-AT" },
	{ value: "YR-BE", label: "YR-BE" },
	{ value: "YR-CA", label: "YR-CA" },
	{ value: "YR-CH", label: "YR-CH" },
	{ value: "YR-DE", label: "YR-DE" },
	{ value: "YR-DK", label: "YR-DK" },
	{ value: "YR-ES", label: "YR-ES" },
	{ value: "YR-FI", label: "YR-FI" },
	{ value: "YR-FR", label: "YR-FR" },
	{ value: "YR-KZ", label: "YR-KZ" },
	{ value: "YR-NL", label: "YR-NL" },
	{ value: "YR-NO", label: "YR-NO" },
	{ value: "YR-RU", label: "YR-RU" },
	{ value: "YR-SE", label: "YR-SE" },
	{ value: "YR-TR", label: "YR-TR" },
	{ value: "YR-UA", label: "YR-UA" },
	{ value: "YR-US", label: "YR-US" }
];

export const loyaltyTypes = [
	{ value: "PRODUCT", label: "PRODUCT" },
	{ value: "DISCOUNT", label: "DISCOUNT" },
	{ value: "TREEPLANTING", label: "TREEPLANTING" },
	{ value: "LATER", label: "LATER" },
];

export const loyaltyRealmCodes = [
	{ value: "YR-FR", label: "YR-FR" },
	{ value: "YR-RU", label: "YR-RU" },
];

const selectRealmOptions = (valuesArray: Array<string>) => {
	if (userService.hasContext('ALL') || userService.hasContext('YR')) {
		return realmCodes;
	}
	const options = [];
	for (const value of valuesArray) {
		const option = {
			value,
			label: value
		};
		options.push(option);
	}
	return options;
}
export const selectRealmOptionsForLoyalty = (valuesArray: Array<string>) => {
	if (userService.hasContext('ALL') || userService.hasContext('YR')) {
		return loyaltyRealmCodes;
	}
	const options = [];
	for (const value of valuesArray) {
		const option = {
			value,
			label: value
		};
		options.push(option);
	}
	return options;
}
export default selectRealmOptions;