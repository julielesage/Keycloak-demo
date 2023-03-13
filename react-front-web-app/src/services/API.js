import axios from "axios";
import userService from "services/userService";

const API = axios.create({
	baseURL: window['env']?.REACT_APP_BX_ADMIN_URL,
});

API.interceptors.request.use(function (config) {
	if (userService.isLoggedIn()) {
		//cb as callback :
		const cb = () => {
			config.headers = {
				Authorization: `Bearer ${userService.getToken()}`,
				'Access-Control-Allow-Origin': '*',
				'Content-Type': 'application/json'
			}
			return Promise.resolve(config);
		};
		return userService.updateToken(cb);
	}
}, function (error) {
	return Promise.reject(error);
});

export default API;
