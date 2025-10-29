/**
 * gold-rate router
 */

export default {
	routes: [
		{
			method: "GET",
			path: "/gold-rates/current",
			handler: "gold-rate.current",
			config: { auth: false },
		},
		{
			method: "GET",
			path: "/gold-rates/history",
			handler: "gold-rate.history",
			config: { auth: false },
		},
	],
};
