import { ERROR_OCCURED, UNAUTHORIZED } from "utils/constants/stringUtil";
import { store } from 'store';
import { appActionTypes } from "store/actionsTypes";

const { UPDATE_ERROR_MESSAGE } = appActionTypes;

const handleServiceError = (error: any, dispatch: typeof store.dispatch) => {
	console.error("error ==>", error);
	if (error.status === 401 || error.response?.status === 401)
		dispatch({ type: UPDATE_ERROR_MESSAGE, payload: UNAUTHORIZED })
	if (error.response?.data?.message)
		dispatch({ type: UPDATE_ERROR_MESSAGE, payload: `*Error ${error.response.status} : ${error?.response?.data?.message}` });
	else
		dispatch({ type: UPDATE_ERROR_MESSAGE, payload: `${ERROR_OCCURED} : ${error}` });
}

export default handleServiceError;