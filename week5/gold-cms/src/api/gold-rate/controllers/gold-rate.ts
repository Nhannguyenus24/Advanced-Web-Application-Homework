/**
 * gold-rate controller
 */

import { factories } from "@strapi/strapi";

export default factories.createCoreController(
  "api::gold-rate.gold-rate",
  ({ strapi }) => ({
    async current(ctx) {
      try {
        const [latest] = await strapi.db
          .query("api::gold-rate.gold-rate")
          .findMany({
            orderBy: { createdAt: "desc" },
            limit: 1,
          });
        if (!latest) return { message: "No data found" };
        return latest;
      } catch (err) {
        ctx.throw(500, err);
      }
    },

    async history(ctx) {
      const records = await strapi.db
        .query("api::gold-rate.gold-rate")
        .findMany({
          orderBy: { timestamp: "asc" },
        });

      return records;
    },
  })
);
