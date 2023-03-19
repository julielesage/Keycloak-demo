process.env.NODE_ENV = "test";
import { app } from 'store/reducers/app.reducer';
import initialState from "store/initialStates/app.initialState";
import { appActionTypes } from "store/actionsTypes";

const {
	TOGGLE_DISPLAY,
	DISPLAY_OFF,
	TOGGLE_HEADER_ACCESS,
	UPDATE_PAGE,
	UPDATE_APP,
	UPDATE_SEARCH_PARAMS,
	TOGGLE_ADVANCE_SEARCH,
	REMOVE_SEARCH,
	UPDATE_ERROR_MESSAGE,
	ADD_DATA,
	REMOVE_DATA
} = appActionTypes;

describe('app reducer', () => {
	it('should handle TOGGLE_DISPLAY', () => {
		expect(app(initialState, { type: TOGGLE_DISPLAY })).toEqual({
			headerAccess: true,
			display: false,
			currentPage: "home",
			errorMessage: null,
			successMessage: null,
			data: null,
			search: {
				advance: false,
				pagination: {},
				params: {},
				results: []
			}
		});
	});
	it('should handle TOGGLE_HEADER_ACCESS', () => {
		expect(app(initialState, { type: TOGGLE_HEADER_ACCESS })).toEqual({
			headerAccess: false,
			display: true,
			currentPage: "home",
			errorMessage: null,
			successMessage: null,
			data: null,
			search: {
				advance: false,
				pagination: {},
				params: {},
				results: []
			}
		});
	});
	it('should handle UPDATE_PAGE', () => {
		expect(app(initialState, { type: UPDATE_PAGE, payload: "new page" })).toEqual({
			headerAccess: true,
			display: true,
			currentPage: "new page",
			errorMessage: null,
			successMessage: null,
			data: null,
			search: {
				advance: false,
				pagination: {},
				params: {},
				results: []
			}
		});
	});
	it('should handle UPDATE_ERROR_MESSAGE', () => {
		expect(app(initialState, { type: UPDATE_ERROR_MESSAGE, payload: "Error : login incorrect" })).toEqual({
			headerAccess: true,
			display: true,
			currentPage: "home",
			data: null,
			errorMessage: "Error : login incorrect",
			successMessage: null,
			search: {
				advance: false,
				pagination: {},
				params: {},
				results: []
			}
		});
	});
	it('should handle ADD_DATA', () => {
		expect(app(initialState, { type: ADD_DATA, payload: "This is the data" })).toEqual({
			headerAccess: true,
			display: true,
			currentPage: "home",
			data: "This is the data",
			errorMessage: null,
			successMessage: null,
			search: {
				advance: false,
				pagination: {},
				params: {},
				results: []
			}
		});
	});
	it('should handle REMOVE_DATA', () => {
		expect(app(initialState, { type: REMOVE_DATA })).toEqual({
			headerAccess: true,
			display: true,
			currentPage: "home",
			data: null,
			errorMessage: null,
			successMessage: null,
			search: {
				advance: false,
				pagination: {},
				params: {},
				results: []
			}
		});
	});
	it('should handle nothing but return state', () => {
		expect(app(initialState, { type: 'other' })).toEqual(initialState);
	});
});