export const searchInitialState = {
	advance: false,
	params: {},
	results: [],
	pagination: {}
}

const appInitialState: IStateApp = {
	headerAccess: true,
	display: true,
	currentPage: "home",
	errorMessage: null,
	successMessage: null,
	search: searchInitialState,
	data: null,
};

export type IStateApp = {
	headerAccess: boolean,
	display: boolean,
	currentPage: string,
	errorMessage: string | null,
	successMessage: string | null,
	search: IStateAppSearch,
	data?: IStateData | null
}

export interface IStateAppSearch {
	advance: boolean | null,
	params: any,
	results: Array<any>,
	pagination: any
}

export interface IStateData {
	id: number | string,
	inputData: any
}
export default appInitialState;