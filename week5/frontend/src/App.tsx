import "./App.css";
import { useCallback, useEffect, useMemo, useState } from "react";
import axios from "axios";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";

interface BackendGoldTypeEntry {
  name: string;
  gia_ban: string;
  gia_mua: string;
  updated_at: string;
}

interface BackendLocationEntry {
  name: string;
  gold_type: BackendGoldTypeEntry[];
}

interface BackendGoldRecord {
  id?: number;
  updated_text: string;
  locations: BackendLocationEntry[];
  createdAt?: string;
}

interface GoldRatePoint {
  buy_price: number;
  sell_price: number;
  timestamp: string;
}

// styles moved to App.css

function App() {
  const [current, setCurrent] = useState<GoldRatePoint | null>(null);
  const [history, setHistory] = useState<GoldRatePoint[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [selectedRange, setSelectedRange] = useState<"day" | "month" | "year">("day");
  const [availableLocations, setAvailableLocations] = useState<string[]>([]);
  const [selectedLocation, setSelectedLocation] = useState<string>("TPHCM");
  const [availableTypes, setAvailableTypes] = useState<string[]>([]);
  const [selectedType, setSelectedType] = useState<string>("SJC");

  const numberFmt = useMemo(
    () =>
      new Intl.NumberFormat(undefined, {
        style: "decimal",
        maximumFractionDigits: 3,
      }),
    []
  );

  const parsePrice = useCallback((value: string | undefined): number => {
    if (!value) return 0;
    const normalized = value.replace(/\./g, "").replace(/\s/g, "");
    const n = Number.parseInt(normalized, 10);
    return Number.isFinite(n) ? n : 0;
  }, []);

  const pickType = useCallback(
    (record: BackendGoldRecord): BackendGoldTypeEntry | null => {
      if (!record?.locations?.length) return null;
      const loc = record.locations.find((l) => l.name === selectedLocation) || record.locations[0];
      if (!loc || !loc.gold_type?.length) return null;
      const t = loc.gold_type.find((g) => g.name === selectedType) || loc.gold_type[0];
      return t || null;
    },
    [selectedLocation, selectedType]
  );

  const fetchData = useCallback(async () => {
    try {
      setError("");
      setLoading(true);
      const [currentRes, historyRes] = await Promise.all([
        axios.get("http://localhost:1337/api/gold-rates/current"),
        axios.get(`http://localhost:1337/api/gold-rates/history?range=${selectedRange}`),
      ]);
      const currentData: BackendGoldRecord = currentRes.data;
      const historyData: BackendGoldRecord[] = historyRes.data || [];

      const locs = (currentData?.locations || []).map((l) => l.name);
      setAvailableLocations(locs);
      if (!locs.includes(selectedLocation) && locs.length) {
        setSelectedLocation(locs.includes("TPHCM") ? "TPHCM" : locs[0]);
      }
      const typeNames = (() => {
        const loc = currentData?.locations?.find((l) => l.name === selectedLocation) || currentData?.locations?.[0];
        return (loc?.gold_type || []).map((t) => t.name);
      })();
      setAvailableTypes(typeNames);
      if (!typeNames.includes(selectedType) && typeNames.length) {
        setSelectedType(typeNames.includes("SJC") ? "SJC" : typeNames[0]);
      }

      const chosen = pickType(currentData);
      const currentPoint: GoldRatePoint | null = chosen
        ? {
            // convert to millions VND per lượng
            buy_price: parsePrice(chosen.gia_mua) / 1000,
            sell_price: parsePrice(chosen.gia_ban) / 1000,
            timestamp: currentData.createdAt || new Date().toISOString(),
          }
        : null;
      setCurrent(currentPoint);

      const histPoints: GoldRatePoint[] = historyData
        .map((rec) => {
          const t = pickType(rec);
          if (!t) return null;
          return {
            // convert to millions VND per lượng
            buy_price: parsePrice(t.gia_mua) / 1000,
            sell_price: parsePrice(t.gia_ban) / 1000,
            timestamp: rec.createdAt || new Date().toISOString(),
          } as GoldRatePoint;
        })
        .filter((x): x is GoldRatePoint => Boolean(x));

      setHistory(histPoints);
    } catch (err) {
      console.error("Error fetching data:", err);
      setError("Unable to fetch gold rates. Please try again later.");
    } finally {
      setLoading(false);
    }
  }, [selectedRange, selectedLocation, selectedType, parsePrice, pickType]);

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000); // refresh every 10s for testing
    return () => clearInterval(interval);
  }, [fetchData]);

  const rangeChips: Array<{ key: "day" | "month" | "year"; label: string }> = [
    { key: "day", label: "1D" },
    { key: "month", label: "1M" },
    { key: "year", label: "1Y" },
  ];

  const change = useMemo(() => {
    if (!history || history.length < 2) return null;
    const first = history[0];
    const last = history[history.length - 1];
    const diff = last.buy_price - first.buy_price;
    const pct = first.buy_price !== 0 ? (diff / first.buy_price) * 100 : 0;
    return { diff, pct };
  }, [history]);

  const xTicks = useMemo(() => {
    if (!history || history.length === 0) return [] as string[];
    const ticks: string[] = [];
    const pushIfBoundary = (d: Date, original: string) => {
      const minutes = d.getMinutes();
      const hours = d.getHours();
      const day = d.getDate();
      if (selectedRange === "day") {
        if (minutes === 0) ticks.push(original); // every hour
      } else if (selectedRange === "month") {
        if (hours === 0 && minutes === 0) ticks.push(original); // each day at 00:00
      } else if (selectedRange === "year") {
        if (day === 1 && hours === 0 && minutes === 0) ticks.push(original); // first day of month
      }
    };
    for (const item of history) {
      const d = new Date(item.timestamp);
      pushIfBoundary(d, item.timestamp);
    }
    // Ensure first and last are included for context
    if (history.length > 0) {
      const firstTs = history[0].timestamp;
      const lastTs = history[history.length - 1].timestamp;
      if (!ticks.includes(firstTs)) ticks.unshift(firstTs);
      if (!ticks.includes(lastTs)) ticks.push(lastTs);
    }
    return ticks;
  }, [history, selectedRange]);

  const formatAxisTick = useCallback(
    (value: string) => {
      const date = new Date(value);
      if (selectedRange === "day") {
        return date.toLocaleTimeString(undefined, {
          hour: "2-digit",
          minute: "2-digit",
        });
      }
      if (selectedRange === "month") {
        // show compact month/day for month view
        return date.toLocaleDateString(undefined, { month: "short", day: "numeric" });
      }
      // year: show month abbrev
      return date.toLocaleDateString(undefined, { month: "short" });
    },
    [selectedRange]
  );

  const formatTooltipLabel = useCallback(
    (value: string) => {
      const date = new Date(value);
      if (selectedRange === "day") {
        return date.toLocaleString();
      }
      if (selectedRange === "month") {
        // date only
        return date.toLocaleDateString(undefined, { year: "numeric", month: "long", day: "numeric" });
      }
      // year: show month and year
      return date.toLocaleDateString(undefined, { year: "numeric", month: "long", day: "numeric" });
    },
    [selectedRange]
  );

  return (
    <div className="app">
      <div className="container">
        <div className="header">
          <span role="img" aria-label="gold">💰</span>
          <h1 className="title">Gold Price Today</h1>
          <span className="badge">Live</span>
        </div>
        <div className="subheader">
          <div className="subtitle">
            Track current and intraday movement of gold prices.
          </div>
          <div className="chips">
            {rangeChips.map((r) => {
              const active = r.key === selectedRange;
              return (
                <button
                  key={r.key}
                  aria-pressed={active}
                  onClick={() => setSelectedRange(r.key)}
                  className={`chip ${active ? "chip--active" : ""}`}
                >
                  {r.label}
                </button>
              );
            })}
          </div>
          <div className="selectors" style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <label>
              <span style={{ marginRight: 6 }}>Location</span>
              <select value={selectedLocation} onChange={(e) => setSelectedLocation(e.target.value)}>
                {availableLocations.map((loc) => (
                  <option key={loc} value={loc}>
                    {loc}
                  </option>
                ))}
              </select>
            </label>
            <label>
              <span style={{ marginRight: 6 }}>Type</span>
              <select value={selectedType} onChange={(e) => setSelectedType(e.target.value)}>
                {availableTypes.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </label>
          </div>
        </div>

        {error && <div className="error">{error}</div>}

        {loading && (
          <div className="loading">Loading latest prices…</div>
        )}

        {current && (
          <div className="currentCard">
            <div className="row">
              <div className="stat">
                <div className="label">Buy Price</div>
                <div className="value">{numberFmt.format(current.buy_price)} million VND / tael</div>
              </div>
              <div className="stat">
                <div className="label">Sell Price</div>
                <div className="value">{numberFmt.format(current.sell_price)} million VND / tael</div>
              </div>
            </div>
            <div className="mt-12 flex gap-12">
              <div className="stat grow">
                <div className="label">Last Updated</div>
                <div className="value">
                  {new Date(current.timestamp).toLocaleString()}
                </div>
              </div>
            </div>
          </div>
        )}

        <div className="chartCard">
          <div className="chartHeader">
            <div className="chartTitle">Intraday Gold Price Chart</div>
            <div className="legend">
              <div className="legendItem">
                <span className="swatch swatch-buy" />
                <span className="legendTextBuy">Buy</span>
              </div>
              <div className="legendItem">
                <span className="swatch swatch-sell" />
                <span className="legendTextSell">Sell</span>
              </div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={320}>
            <LineChart data={history}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
              <XAxis
                dataKey="timestamp"
                tickFormatter={(v) => formatAxisTick(String(v))}
                stroke="#9ca3af"
                minTickGap={12}
                ticks={xTicks}
              />
              <YAxis stroke="#9ca3af" tickFormatter={(v) => numberFmt.format(Number(v))} />
              <Tooltip
                contentStyle={{
                  background: "#0b1220",
                  border: "1px solid rgba(255,255,255,0.08)",
                  borderRadius: 8,
                  color: "#e5e7eb",
                }}
                labelFormatter={(v) => formatTooltipLabel(String(v))}
                formatter={(value: number | string, name: string) => [
                  typeof value === "number" ? `${numberFmt.format(value)} million VND / tael` : value,
                  name,
                ]}
              />
              <Line
                type="monotone"
                dataKey="buy_price"
                stroke="#a78bfa"
                name="Buy Price"
                dot={false}
                strokeWidth={2}
              />
              <Line
                type="monotone"
                dataKey="sell_price"
                stroke="#34d399"
                name="Sell Price"
                dot={false}
                strokeWidth={2}
              />
            </LineChart>
          </ResponsiveContainer>
          <div className="footerBar">
            <div>
              {change && (
                <span className={change.diff >= 0 ? "trendUp" : "trendDown"}>
                  {change.diff >= 0 ? "▲" : "▼"} {numberFmt.format(Math.abs(change.diff))} ({numberFmt.format(Math.abs(change.pct))}%)
                </span>
              )}
            </div>
            <div>Data refreshes every 10 seconds.</div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
