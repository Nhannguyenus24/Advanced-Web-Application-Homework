export default {
  "*/10 * * * * *": async ({ strapi }) => {
  // "0 * * * *": async ({ strapi }) => {
    const mock = {
      buy_price: 0 + Math.floor(Math.random() * 10000),
      sell_price: 0 + Math.floor(Math.random() * 10000),
      unit: "million VND / tael",
      timestamp: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
    };

    await strapi.db.query("api::gold-rate.gold-rate").create({ data: mock });

    console.log("[CRON] Added record:", mock);
  },
};

