const config = () => {
	console.log(process.env.NODE_ENV)
	if (process.env.NODE_ENV === 'test' || process.env.NODE_ENV === 'development')
		return {
			url: process.env.REACT_APP_KEYCLOAK_AUTH_URL,
			realm: process.env.REACT_APP_KEYCLOAK_REALM,
			clientId: process.env.REACT_APP_KEYCLOAK_CLIENTID
		};
	else 
		return {
			url: window['env'].REACT_APP_KEYCLOAK_AUTH_URL,
			realm: window['env'].REACT_APP_KEYCLOAK_REALM,
			clientId: window['env'].REACT_APP_KEYCLOAK_CLIENTID
		};
}

export default config;
