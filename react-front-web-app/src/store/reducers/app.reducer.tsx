import initialState, { searchInitialState } from "store/initialStates/app.initialState";
import { Action } from "utils/typesHelpers/types";
import { appActionTypes } from "store/actionsTypes";

const {
	UPDATE_SEARCH_PARAMS,
	TOGGLE_ADVANCE_SEARCH,
	REMOVE_SEARCH,
	UPDATE_ERROR_MESSAGE,
	REMOVE_ERROR_MESSAGE,
	UPDATE_SUCCESS_MESSAGE,
	REMOVE_SUCCESS_MESSAGE,
	ADD_DATA,
	REMOVE_DATA,
	UPDATE_PAGENUMBER_SEARCH_PARAM
} = appActionTypes;

export const app = (state = initialState, action: Action) => {
	if (action && action.type) {

		switch (action.type) {

			case UPDATE_SEARCH_PARAMS: {
				return { ...state, search: { ...state.search, params: action.payload } };
			}

			case UPDATE_PAGENUMBER_SEARCH_PARAM: {
				const newParams = { ...state.search.params, pageNumber: action.payload };
				return { ...state, search: { ...state.search, params: newParams } };
			}

			case TOGGLE_ADVANCE_SEARCH:
				return {
					...state, search: {
						...state.search,
						advance: !state.search.advance
					}
				};

			case REMOVE_SEARCH:
				return { ...state, search: searchInitialState }

			case UPDATE_ERROR_MESSAGE:
				return { ...state, errorMessage: action.payload }

			case REMOVE_ERROR_MESSAGE:
				return { ...state, errorMessage: null }

			case UPDATE_SUCCESS_MESSAGE:
				return { ...state, successMessage: action.payload }

			case REMOVE_SUCCESS_MESSAGE:
				return { ...state, successMessage: null }

			case ADD_DATA:
				return { ...state, data: action.payload }

			case REMOVE_DATA:
				return { ...state, data: null }

			default:
				return { ...state };
		}
	} else return state;
}
