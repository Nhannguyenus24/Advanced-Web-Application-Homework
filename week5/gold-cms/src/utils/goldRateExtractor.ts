import axios from "axios";

const URL = "https://edge-api.pnj.io/ecom-frontend/v3/get-gold-price";

export async function crawlGoldRates() {
  const res = await axios.get(URL);
  return res.data;
}
