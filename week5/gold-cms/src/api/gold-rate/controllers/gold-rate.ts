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
      try {
        const range = (ctx.query?.range as string) || "day";
        const now = new Date();
        const cutoff = new Date(now);
        if (range === "month") {
          cutoff.setMonth(now.getMonth() - 1);
        } else if (range === "year") {
          cutoff.setFullYear(now.getFullYear() - 1);
        } else {
          cutoff.setDate(now.getDate() - 1);
        }

        const records = await strapi.db
          .query("api::gold-rate.gold-rate")
          .findMany({
            where: { createdAt: { $gte: cutoff } },
            orderBy: { createdAt: "asc" },
          });

        return records;
      } catch (err) {
        ctx.throw(500, err);
      }
    },
  })
);
