process.env.NODE_ENV = "test";
import Header from 'components/design/Header';
import { fireEvent, render } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import { Router } from 'react-router-dom';
import history from "utils/history";
import { store } from "store";
import userService from "services/userService";
import registerIcons from 'utils/registerIcons';
import { canAccessJobRunner, canAccessLoyalty } from 'controllers/userController';

/* ------------------------------------------------------------------
  §§ MOCKS
------------------------------------------------------------------ */

registerIcons();
jest.mock('store');
jest.mock('services/userService');
userService.getUsername = jest.fn(() => "Julie");
store.getState = jest.fn(() => {
	return {
		app: {
			currentPage: "customer",
			display: false,
			headerAccess: true,
			errorMessage: "* Sorry, customer unknown"
		}
	}
})
jest.mock('components/buttons/JobRunnerButton', () => {
	return {
		__esModule: true,
		A: true,
		default: () => {
			return <div>job runner button</div>;
		},
	}
});
jest.mock('components/buttons/SearchToggleButton', () => {
	return {
		__esModule: true,
		A: true,
		default: () => {
			return <div>search toggle button</div>;
		},
	}
});
jest.mock('components/buttons/LogoutButton', () => {
	return {
		__esModule: true,
		A: true,
		default: () => {
			return <div>logout button</div>;
		},
	}
});
jest.mock('components/buttons/LoyaltyButton', () => {
	return {
		__esModule: true,
		A: true,
		default: () => {
			return <div>loyalty button</div>;
		},
	}
});

/* ------------------------------------------------------------------
  §§ TESTS
------------------------------------------------------------------ */

describe("application header", () => {

	it("should say render, show logo + 3 buttons and say hello to user", async () => {
		userService.hasRole = jest.fn(() => false);
		const { getByText, getByAltText } = render(
			<Router history={history}>
				<Header />
			</Router>);
		getByText("Hello Julie");
		getByText("search toggle button");
		getByText("logout button");
		getByAltText("logo");
	})

	it("should return to home at logo click", async () => {
		userService.hasRole = jest.fn(() => false);
		const { getByAltText } = render(
			<Router history={history}>
				<Header />
			</Router>);
		await act(async () => {
			fireEvent.click(getByAltText("logo"));
		});
		expect(location.pathname).toBe("/");
	});

	it("should show loyalty button if role is MANAGER", () => {
		userService.hasRole = jest.fn(() => true);
		const { getByText } = render(
			<Router history={history}>
				<Header />
			</Router>
		);
		getByText("loyalty button");
	})

	it("should show job runner button if role is ADMIN", () => {
		userService.hasRole = jest.fn(() => true);
		const { getByText } = render(
			<Router history={history}>
				<Header />
			</Router>
		);
		getByText("job runner button");
	})
})