process.env.NODE_ENV = "test";

import React from "react";
import { render } from "@testing-library/react";
import JobRunnerButton from "components/buttons/JobRunnerButton";
import { Router } from 'react-router-dom';
import registerIcons from 'utils/registerIcons';
import history from "utils/history";

describe('JobRunnerButton', () => {
	registerIcons();
	it('should navigate to jobrunner page when link is clicked', () => {
		history.push = jest.fn();
		const { getByTestId } = render(
			<Router history={history}>
				<JobRunnerButton />
			</Router>)
		getByTestId('button').click();
		expect(history.push).toHaveBeenCalledWith('/job-runner');
	})
});