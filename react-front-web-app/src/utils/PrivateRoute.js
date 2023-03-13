import { Route, Navigate } from 'react-router-dom';

const PrivateRoute = ({ hasAccess, component: Component, ...rest }) => (
	<Route {...rest} render={props => {
		if (hasAccess)
			return <Component {...props} />
		else
			return <Navigate to={{ pathname: '/' }} />
	}} />
)

export default PrivateRoute;
