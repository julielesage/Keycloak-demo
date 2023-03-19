process.env.NODE_ENV = "test";

import React from "react";
import LogoutButton from 'components/buttons/LogoutButton';
import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import registerIcons from 'utils/registerIcons';
import userService from 'services/userService';

registerIcons();
jest.mock('services/userService');
userService.doLogout = jest.fn();
jest.mock('react-redux', () => ({
	useSelector: jest.fn(() => mockState),
	useDispatch: jest.fn(),
}));

describe("logout button", () => {
	it("should call logout and show login modal at click", () => {
		const { getByText } = render(<LogoutButton />);
		fireEvent.click(getByText("Logout"));
		expect(userService.doLogout).toHaveBeenCalled();
	})
})