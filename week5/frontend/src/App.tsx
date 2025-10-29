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

interface GoldRate {
  buy_price: number;
  sell_price: number;
  unit: string;
  timestamp: string;
}

const styles: { [key: string]: React.CSSProperties } = {
  app: {
    minHeight: "100vh",
    background: "linear-gradient(180deg, #0f172a 0%, #111827 100%)",
    padding: 24,
  },
  container: {
    width: "100%",
    margin: 0,
    fontFamily:
      "Inter, ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial",
    color: "#e5e7eb",
  },
  header: {
    display: "flex",
    alignItems: "center",
    gap: 12,
    marginBottom: 16,
  },
  title: {
    fontSize: 28,
    fontWeight: 800,
    letterSpacing: 0.2,
    color: "#fbbf24",
  },
  subtitle: {
    marginTop: 4,
    color: "#9ca3af",
    fontSize: 14,
  },
  currentCard: {
    background:
      "linear-gradient(180deg, rgba(251,191,36,0.10) 0%, rgba(253,230,138,0.06) 100%)",
    border: "1px solid rgba(251,191,36,0.25)",
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
    boxShadow: "0 10px 30px rgba(0,0,0,0.25)",
  },
  row: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: 12,
  },
  stat: {
    background: "rgba(17,24,39,0.5)",
    border: "1px solid rgba(255,255,255,0.06)",
    borderRadius: 12,
    padding: 12,
  },
  label: {
    color: "#9ca3af",
    fontSize: 12,
  },
  value: {
    fontSize: 22,
    fontWeight: 700,
    color: "#f3f4f6",
  },
  chartCard: {
    background: "#0b1220",
    border: "1px solid rgba(255,255,255,0.06)",
    borderRadius: 16,
    padding: 16,
    boxShadow: "0 10px 30px rgba(0,0,0,0.25)",
  },
  chartTitle: {
    fontSize: 16,
    fontWeight: 700,
    marginBottom: 8,
    color: "#e5e7eb",
  },
  footer: {
    marginTop: 12,
    color: "#9ca3af",
    fontSize: 12,
    textAlign: "right",
  },
  badge: {
    display: "inline-block",
    padding: "2px 8px",
    background: "rgba(99,102,241,0.15)",
    color: "#a5b4fc",
    border: "1px solid rgba(99,102,241,0.35)",
    borderRadius: 9999,
    fontSize: 12,
    fontWeight: 600,
  },
  loading: {
    padding: 16,
    textAlign: "center",
    color: "#9ca3af",
  },
  error: {
    padding: 12,
    background: "rgba(239,68,68,0.12)",
    border: "1px solid rgba(239,68,68,0.35)",
    color: "#fecaca",
    borderRadius: 12,
    marginBottom: 12,
  },
};

function App() {
  const [current, setCurrent] = useState<GoldRate | null>(null);
  const [history, setHistory] = useState<GoldRate[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [selectedRange, setSelectedRange] = useState<"day" | "week" | "month">("day");

  const numberFmt = useMemo(
    () =>
      new Intl.NumberFormat(undefined, {
        style: "decimal",
        maximumFractionDigits: 2,
      }),
    []
  );


  const fetchData = useCallback(async () => {
    try {
      setError("");
      setLoading(true);
      const [currentRes, historyRes] = await Promise.all([
        axios.get("http://localhost:1337/api/gold-rates/current"),
        axios.get(`http://localhost:1337/api/gold-rates/history?range=${selectedRange}`),
      ]);
      setCurrent(currentRes.data);
      setHistory(historyRes.data);
    } catch (err) {
      console.error("Error fetching data:", err);
      setError("Unable to fetch gold rates. Please try again later.");
    } finally {
      setLoading(false);
    }
  }, [selectedRange]);

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000); // refresh every 10s for testing
    return () => clearInterval(interval);
  }, [fetchData]);

  const rangeChips: Array<{ key: "day" | "week" | "month"; label: string }> = [
    { key: "day", label: "1D" },
    { key: "week", label: "1W" },
    { key: "month", label: "1M" },
  ];

  const change = useMemo(() => {
    if (!history || history.length < 2) return null;
    const first = history[0];
    const last = history[history.length - 1];
    const diff = last.buy_price - first.buy_price;
    const pct = first.buy_price !== 0 ? (diff / first.buy_price) * 100 : 0;
    return { diff, pct };
  }, [history]);

  return (
    <div style={styles.app}>
      <div style={styles.container}>
        <div style={styles.header}>
          <span role="img" aria-label="gold">💰</span>
          <h1 style={styles.title}>Gold Price Today</h1>
          <span style={styles.badge}>Live</span>
        </div>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
          <div style={styles.subtitle}>
            Track current and intraday movement of gold prices.
          </div>
          <div style={{ display: "flex", gap: 8 }}>
            {rangeChips.map((r) => {
              const active = r.key === selectedRange;
              return (
                <button
                  key={r.key}
                  aria-pressed={active}
                  onClick={() => setSelectedRange(r.key)}
                  style={{
                    cursor: "pointer",
                    border: "1px solid",
                    borderColor: active ? "#fbbf24" : "rgba(255,255,255,0.15)",
                    color: active ? "#111827" : "#e5e7eb",
                    background: active ? "#fbbf24" : "transparent",
                    padding: "6px 10px",
                    borderRadius: 9999,
                    fontWeight: 700,
                    fontSize: 12,
                  }}
                >
                  {r.label}
                </button>
              );
            })}
          </div>
        </div>

        {error && <div style={styles.error}>{error}</div>}

        {loading && (
          <div style={styles.loading}>Loading latest prices…</div>
        )}

        {current && (
          <div style={styles.currentCard}>
            <div style={styles.row}>
              <div style={styles.stat}>
                <div style={styles.label}>Buy Price</div>
                <div style={styles.value}>{numberFmt.format(current.buy_price)}</div>
              </div>
              <div style={styles.stat}>
                <div style={styles.label}>Sell Price</div>
                <div style={styles.value}>{numberFmt.format(current.sell_price)}</div>
              </div>
            </div>
            <div style={{ marginTop: 12, display: "flex", gap: 12 }}>
              <div style={{ ...styles.stat, flex: 1 }}>
                <div style={styles.label}>Unit</div>
                <div style={styles.value}>{current.unit}</div>
              </div>
              <div style={{ ...styles.stat, flex: 1 }}>
                <div style={styles.label}>Last Updated</div>
                <div style={styles.value}>
                  {new Date(current.timestamp).toLocaleString()}
                </div>
              </div>
            </div>
          </div>
        )}

        <div style={styles.chartCard}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
            <div style={styles.chartTitle}>Intraday Gold Price Chart</div>
            <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
              <div style={{ display: "flex", gap: 6, alignItems: "center" }}>
                <span style={{ width: 10, height: 10, background: "#a78bfa", borderRadius: 2, display: "inline-block" }} />
                <span style={{ color: "#c7d2fe", fontSize: 12 }}>Buy</span>
              </div>
              <div style={{ display: "flex", gap: 6, alignItems: "center" }}>
                <span style={{ width: 10, height: 10, background: "#34d399", borderRadius: 2, display: "inline-block" }} />
                <span style={{ color: "#bbf7d0", fontSize: 12 }}>Sell</span>
              </div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={320}>
            <LineChart data={history}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
              <XAxis
                dataKey="timestamp"
                tickFormatter={(v) => new Date(v).toLocaleTimeString()}
                stroke="#9ca3af"
              />
              <YAxis stroke="#9ca3af" />
              <Tooltip
                contentStyle={{
                  background: "#0b1220",
                  border: "1px solid rgba(255,255,255,0.08)",
                  borderRadius: 8,
                  color: "#e5e7eb",
                }}
                labelFormatter={(v) => new Date(v).toLocaleString()}
                formatter={(value: number | string, name: string) => [
                  typeof value === "number" ? numberFmt.format(value) : value,
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
          <div style={{ ...styles.footer, display: "flex", justifyContent: "space-between" }}>
            <div>
              {change && (
                <span style={{
                  color: change.diff >= 0 ? "#86efac" : "#fca5a5",
                  fontWeight: 700,
                }}>
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
