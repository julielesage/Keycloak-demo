import React from "react";
import { useNavigation } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useHover } from 'utils/customHooks';

const JobRunnerButton = () => {
	const navigate: any = useNavigation()
	const [hoverMe, isHovered] = useHover<HTMLDivElement>();

	return (
		<div data-testid="button" ref={hoverMe} className="aligned column cursor mr-30" onClick={(e) => {
			e.preventDefault()
			navigate('/job-runner')
		}}>
			<FontAwesomeIcon
				icon="cog"
				size="2x"
				className={isHovered ? "able" : "disable"}
			/>
			<div className={isHovered ? "fs-10 fw-700 able" : "fs-10 fw-700 disable"} > Job runners</div>
		</div >
	);
};

export default JobRunnerButton;
