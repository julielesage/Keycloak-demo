import {createStore, combineReducers, compose, applyMiddleware} from "redux";

import { customer } from "store/reducers/customer.reducer";
import { app } from "store/reducers/app.reducer";
import { loyaltyRewards } from 'store/reducers/loyalty.reducer';
import thunk from "redux-thunk";

export interface ApplicationState {
	//customer: any;
	app: any;
	loyaltyRewards: any;
}

const SparkApp = combineReducers<ApplicationState>({
	//customer,
	app,
	loyaltyRewards,
});

export const store = createStore(SparkApp, compose(
	applyMiddleware(thunk),
	// for dev using redux dev tools in chrome :
	window['env']?.REACT_APP_ENV === "dev" && (window as any).__REDUX_DEVTOOLS_EXTENSION__ ?
	(window as any).__REDUX_DEVTOOLS_EXTENSION__ && (window as any).__REDUX_DEVTOOLS_EXTENSION__()
	 : (a : any) => a
));

// expose store when run in Cypress
// @ts-ignore
if (window && window.Cypress) {
	// @ts-ignore
	window.store = store
}
