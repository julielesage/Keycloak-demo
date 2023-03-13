import React from 'react'
import { Router, Routes, Route } from 'react-router-dom'
import { createMemoryHistory } from 'history'
import userService from 'services/userService'
import userController, { canAccessJobRunner, canAccessLoyalty } from 'controllers/userController'
import Header from 'components/layout/Header'
import Home from 'containers/Home'
import PrivateRoute from 'utils/PrivateRoute'
import JobRunner from 'containers/JobRunner'
import Loyalty from 'containers/Loyalty'
import 'styles/style.scss';

function App() {
  const { doLogout } = userService
  const history = createMemoryHistory()


	if (!userController.isAllowed()) {
		setTimeout(() => doLogout(), 1500);
  }

  return userController.isAllowed() ?

		<Router navigator={history} location={history.location} >
			<Header />
			<div className={`wrapper class_theme`}>
				<Routes>
					<Route path="/">
						<Home />
					</Route>
					<PrivateRoute path="/job-runner" component={JobRunner} hasAccess={canAccessJobRunner()} />
					<PrivateRoute path="/loyalty" component={Loyalty} hasAccess={canAccessLoyalty()} />
				</Routes>
			</div>
		</Router>

		:

		<div className="class_theme">
			<div className="h_100vh centered aligned bg_color_show-btn white strong">
				Sorry you don't have necessary role for this app.
			</div>
		</div>
		;
}

export default App;
