import userService from "services/userService";
// import LoyaltyButton from 'components/buttons/LoyaltyButton'
// import LogoutButton from 'components/buttons/LogoutButton'
// import JobRunnerButton from "components/buttons/JobRunnerButton";
import { canAccessLoyalty, canAccessJobRunner } from 'controllers/userController';

const Header = () => {
	const { getUsername } = userService;

	return (
		<div className="class_theme">
			<header className="aligned">
				<ul className="wrapper row space-between aligned">
					<div className="row aligned">
						<li className="staffName">Hello {getUsername()}</li>
						{/* {canAccessLoyalty() && <LoyaltyButton />}
						{canAccessJobRunner() && <JobRunnerButton />}
						<LogoutButton /> */}
					</div>
				</ul>
			</header>
		</div>
	);
};

export default Header;
