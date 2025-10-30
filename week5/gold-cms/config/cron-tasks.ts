export interface GoldResponse {
  updated_text: string;
  locations: LocationEntry[];
}

export interface LocationEntry {
  name: string;
  gold_type: GoldTypeEntry[];
}

export interface GoldTypeEntry {
  name: string;
  gia_ban: string;
  gia_mua: string;
  updated_at: string;
}

const generateRandomMockData = (): GoldResponse => {
  const now = new Date();
  const currentTime = now.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit', 
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });

  // Base prices with some randomization
  const basePrice = 145000 + Math.random() * 6000; // Random between 145k-151k
  const priceDiff = 2000 + Math.random() * 2000; // Random difference 2k-4k

  const generateGoldPrice = (variation = 0) => {
    const sellPrice = Math.round((basePrice + variation) / 50) * 50; // Round to nearest 50
    const buyPrice = Math.round((sellPrice - priceDiff) / 50) * 50;
    return {
      gia_ban: sellPrice.toLocaleString('vi-VN'),
      gia_mua: buyPrice.toLocaleString('vi-VN')
    };
  };

  const pnjPrices = generateGoldPrice(Math.random() * 500 - 250);
  const sjcPrices = generateGoldPrice(Math.random() * 500 - 250);

  return {
    updated_text: `Giá vàng ngày: ${currentTime}`,
    locations: [
      {
        name: "TPHCM",
        gold_type: [
          {
            name: "PNJ",
            ...pnjPrices,
            updated_at: currentTime,
          },
          {
            name: "SJC",
            ...sjcPrices,
            updated_at: currentTime,
          },
        ],
      },
      {
        name: "Hà Nội",
        gold_type: [
          {
            name: "PNJ",
            ...pnjPrices,
            updated_at: currentTime,
          },
          {
            name: "SJC",
            ...sjcPrices,
            updated_at: currentTime,
          },
        ],
      },
      {
        name: "Đà Nẵng",
        gold_type: [
          {
            name: "PNJ",
            ...pnjPrices,
            updated_at: currentTime,
          },
          {
            name: "SJC",
            ...sjcPrices,
            updated_at: currentTime,
          },
        ],
      },
      {
        name: "Miền Tây",
        gold_type: [
          {
            name: "PNJ",
            ...pnjPrices,
            updated_at: currentTime,
          },
          {
            name: "SJC",
            ...sjcPrices,
            updated_at: currentTime,
          },
        ],
      },
      {
        name: "Tây Nguyên",
        gold_type: [
          {
            name: "PNJ",
            ...pnjPrices,
            updated_at: currentTime,
          },
          {
            name: "SJC",
            ...sjcPrices,
            updated_at: currentTime,
          },
        ],
      },
      {
        name: "Đông Nam Bộ",
        gold_type: [
          {
            name: "PNJ",
            ...pnjPrices,
            updated_at: currentTime,
          },
          {
            name: "SJC",
            ...sjcPrices,
            updated_at: currentTime,
          },
        ],
      },
      {
        name: "Giá vàng nữ trang",
        gold_type: [
          {
            name: "Nhẫn Trơn PNJ 999.9",
            ...generateGoldPrice(-200),
            updated_at: currentTime,
          },
          {
            name: "Vàng Kim Bảo 999.9",
            ...generateGoldPrice(-150),
            updated_at: currentTime,
          },
          {
            name: "Vàng Phúc Lộc Tài 999.9",
            ...generateGoldPrice(-100),
            updated_at: currentTime,
          },
          {
            name: "Vàng nữ trang 999.9",
            ...generateGoldPrice(-400),
            updated_at: currentTime,
          },
          {
            name: "Vàng nữ trang 999",
            ...generateGoldPrice(-550),
            updated_at: currentTime,
          },
          {
            name: "Vàng nữ trang 9920",
            ...generateGoldPrice(-1500),
            updated_at: currentTime,
          },
          {
            name: "Vàng nữ trang 99",
            ...generateGoldPrice(-1800),
            updated_at: currentTime,
          },
          {
            name: "Vàng 916 (22K)",
            ...generateGoldPrice(-12000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 750 (18K)",
            ...generateGoldPrice(-36000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 680 (16.3K)",
            ...generateGoldPrice(-46000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 650 (15.6K)",
            ...generateGoldPrice(-50000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 610 (14.6K)",
            ...generateGoldPrice(-56000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 585 (14K)",
            ...generateGoldPrice(-60000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 416 (10K)",
            ...generateGoldPrice(-85000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 375 (9K)",
            ...generateGoldPrice(-91000),
            updated_at: currentTime,
          },
          {
            name: "Vàng 333 (8K)",
            ...generateGoldPrice(-98000),
            updated_at: currentTime,
          },
        ],
      },
    ],
  };
};

export default {
  "*/10 * * * * *": async ({ strapi }) => {
    // "0 * * * *": async ({ strapi }) => {
    const mock = generateRandomMockData();

    await strapi.db.query("api::gold-rate.gold-rate").create({ data: mock });

    console.log("[CRON] Added record:", mock);
  },
};
