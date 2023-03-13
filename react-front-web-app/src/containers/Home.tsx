import React, { useEffect } from "react"

const Home = () => {

	return (
		<div className="pt-100 search">
      You are _ from _ and have role of _
		</div >
	);
};

interface IPageState {
	action?: string,
	firstName?: string,
	lastName: string,
}

export default Home;
