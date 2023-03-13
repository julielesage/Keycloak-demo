import Keycloak from 'keycloak-js'
import config from 'utils/keycloakConfig'
import { realmCodes } from 'utils/select/selectRealmOptions'

const _kc = new Keycloak(config());

/**
 * Initializes Keycloak instance and calls the provided callback function if successfully authenticated.
 *
 * @param onAuthenticatedCallback
 */
const initKeycloak = (onAuthenticatedCallback) => {
	_kc.init({
		onLoad: 'login-required',
	})
		.then((authenticated) => {
			onAuthenticatedCallback();
		});
};

const doLogin = _kc.login;

const doLogout = _kc.logout;

const getToken = () => _kc.token;

const isLoggedIn = () => !!_kc.token;

const updateToken = (successCallback) =>
	_kc.updateToken(5)
		.then(successCallback)
		.catch(doLogin);

const getUsername = () => _kc.tokenParsed?.given_name;

const hasScope = (scope) => {
	if (_kc.tokenParsed?.realm_access?.roles) {
		for (const role of _kc.tokenParsed?.realm_access?.roles) {
			if (role.split('_')[0] === scope)
				return true;
		}
	}
	return false;
}

const hasContext = (context) => {
	if (_kc.tokenParsed?.realm_access?.roles) {
		for (const role of _kc.tokenParsed?.realm_access?.roles) {
			if (role.split('_')[1] === context)
				return true;
		}
	}
	return false;
}

const hasContextPart = (first2letters) => {
	if (_kc.tokenParsed?.realm_access?.roles) {
		for (const role of _kc.tokenParsed?.realm_access?.roles) {
			if (role.split('_')[1] && role.split('_')[1].slice(0, 2) === first2letters)
				return true;
		}
	}
	return false;
}

const hasRole = (userRole) => {
	if (_kc.tokenParsed?.realm_access?.roles) {
		for (const role of _kc.tokenParsed?.realm_access?.roles) {
			if (role.split('_')[2] === userRole)
				return true;
		}
	}
	return false;
}

const isContext = (context) => {
	for (const realmCode of realmCodes) {
		if (realmCode.value === context)
			return true;
	}
	return false;
}

const getContexts = () => {
	const contexts = [];
	if (_kc.tokenParsed?.realm_access?.roles) {
		for (const role of _kc.tokenParsed?.realm_access?.roles) {
			role.split('_')[1] &&
				isContext(role.split('_')[1]) &&
				contexts.push(role.split('_')[1]);
		}
	}
	return contexts;
}

const userService = {
	initKeycloak,
	doLogin,
	doLogout,
	isLoggedIn,
	getToken,
	updateToken,
	getUsername,
	hasScope,
	hasContext,
	hasContextPart,
	hasRole,
	getContexts,
};

export default userService;
