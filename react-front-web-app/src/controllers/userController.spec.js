process.env.NODE_ENV = "test";
/* ------------------------------------------------------------------
  § USER CONTROLLER
  $$ CAN ACCESS JOB RUNNER PAGE
------------------------------------------------------------------ */
import userController, {
	canAccessLoyalty,
	canDeleteCustomer,
	canGetCustomerTasks,
	canDownloadGDPR,
	canAccessJobRunner
} from "controllers/userController";
import userService from "services/userService";

/* ------------------------------------------------------------------
  §§ MOCKS
------------------------------------------------------------------ */
jest.mock('keycloak-js');
jest.mock('services/userService');

/* ------------------------------------------------------------------
  §§ TESTS
------------------------------------------------------------------ */

describe("userService using Keycloak", () => {

	it("should be allowed", () => {
		userService.hasScope = jest.fn(() => true);
		expect(userController.isAllowed()).toBeTruthy();
	})

	// canAccessLoyalty
	it("should return false if has not allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => false);
		// THEN
		expect(canAccessLoyalty()).toBeFalsy();
	})
	it("should return true if has allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => true);
		// THEN
		expect(canAccessLoyalty()).toBeTruthy();
	})

	// canDeleteCustomer
	it("should return true if has allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => true);
		// THEN
		expect(canDeleteCustomer()).toBeTruthy();
	})
	it("should return false if has not allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => false);
		// THEN
		expect(canDeleteCustomer()).toBeFalsy();
	})

	// canDeleteCustomer
	it("should return false if has not allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => false);
		// THEN
		expect(canGetCustomerTasks()).toBeFalsy();
	})
	it("should return true if has allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => true);
		// THEN
		expect(canGetCustomerTasks()).toBeTruthy();
	})

	// canDownloadGDPR
	it("should return false if has not allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => false);
		// THEN
		expect(canDownloadGDPR()).toBeFalsy();
	})
	it("should return true if has allowed role", () => {
		// WHEN
		userService.hasRole = jest.fn(() => true);
		// THEN
		expect(canDownloadGDPR()).toBeTruthy();
	})
})

/* ------------------------------------------------------------------
  §§ TEST : CAN  ACCESS JOB RUNNER PAGE
------------------------------------------------------------------ */

describe("checing if user role can access job runner page", () => {
	it("should return false if is manager", () => {
		// WHEN
		userService.hasRole = jest.fn(() => false);
		// THEN
		expect(canAccessJobRunner()).toBeFalsy();
	})
	it("should return true if prod or admin", () => {
		// WHEN
		userService.hasRole = jest.fn(() => true);
		// THEN
		expect(canAccessJobRunner()).toBeTruthy();
	})
})