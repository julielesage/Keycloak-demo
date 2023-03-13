import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import userService from "services/userService";
import { useHover } from 'utils/customHooks';
import 'styles/style.scss';

const LogoutButton: React.FC = () => {
	const [hoverLogout, logoutIsHovered] = useHover<HTMLDivElement>();

	const handleLogOut = (e: React.FormEvent) => {
		e.preventDefault();
		userService.doLogout();
	};

	return (
		<div className="aligned column cursor" ref={hoverLogout} onClick={handleLogOut}>
			<FontAwesomeIcon
				icon={["fas", "sign-out-alt"]}
				size="2x"
				className={logoutIsHovered ? "able" : "disable"}
			/>
			<li className={logoutIsHovered ? "fs-10 fw-700 able" : "fs-10 fw-700 disable"}>Logout</li>
		</div>
	);
};

export default LogoutButton;