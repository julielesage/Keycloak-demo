import React from "react";
import LoyaltyButton from 'components/buttons/LoyaltyButton';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Router } from 'react-router-dom';
import registerIcons from 'utils/registerIcons';
import history from "utils/history";

describe('LoyaltyButton', () => {
	registerIcons();
	it('should navigate to Loyalty page when link is clicked', () => {
		history.push = jest.fn();
		const { getByTestId } = render(
			<Router history={history}>
				<LoyaltyButton />
			</Router>)
		getByTestId('button').click();
		expect(history.push).toHaveBeenCalledWith('/loyalty/reward');
	})
});