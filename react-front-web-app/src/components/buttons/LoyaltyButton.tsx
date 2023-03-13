import React from "react";
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useHover } from 'utils/customHooks';

const LoyaltyButton = () => {
	const navigate: any = useNavigate();
	const [hoverMe, isHovered] = useHover<HTMLDivElement>();

	return (
		<div data-testid="button" ref={hoverMe} className="aligned column cursor mr-30" onClick={(e) => {
			e.preventDefault();
			navigate('/loyalty/reward')
		}}>
			<FontAwesomeIcon
				icon={["fas", "hand-holding-heart"]}
				size="2x"
				className={isHovered ? "able" : "disable"}
			/>
			<div className={isHovered ? "fs-10 fw-700 able" : "fs-10 fw-700 disable"} > Loyalty</div>
		</div >
	);
};

export default LoyaltyButton;
