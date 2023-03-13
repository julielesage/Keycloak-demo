import loyaltyAccess from "utils/config/loyaltyAccess";
import customerAccess from "utils/config/customerAccess";
import jobRunnerAccess from "utils/config/jobRunnerAccess";
import userService from "services/userService";

export const canAccessLoyalty = () => {
	if (loyaltyAccess.length > 0) {
		for (const allowedRole of loyaltyAccess) {
			if (userService.hasRole(allowedRole))
				return true;
		}
	}
	return false;
}

export const canAccessJobRunner = () => {
	if (jobRunnerAccess.length > 0) {
		for (const allowedRole of jobRunnerAccess) {
			if (userService.hasRole(allowedRole))
				return true;
		}
	}
	return false;
}

export const canDeleteCustomer = () => {
	if (customerAccess?.delete?.length > 0) {
		for (const allowedRole of customerAccess.delete) {
			if (userService.hasRole(allowedRole))
				return true;
		}
	}
	return false;
}

export const canGetCustomerTasks = () => {
	if (customerAccess?.tasks?.length > 0) {
		for (const allowedRole of customerAccess.tasks) {
			if (userService.hasRole(allowedRole))
				return true;
		}
	}
	return false;
}

export const canDownloadGDPR = () => {
	if (customerAccess?.tasks?.length > 0) {
		for (const allowedRole of customerAccess.GDPRFile) {
			if (userService.hasRole(allowedRole))
				return true;
		}
	}
	return false;
}

const isAllowed = () => {
	const { hasScope } = userService;
	if (hasScope("GLOBAL") || hasScope("BRAND") || hasScope("SITE"))
		return true;
	return false;
}

const userController = {
	isAllowed
}

export default userController;
