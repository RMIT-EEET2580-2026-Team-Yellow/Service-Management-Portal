import { useState } from "react";
import { Link } from "react-router-dom";

/**
 * MOCK UI ONLY — no backend wired up yet (3.4.S3, M3).
 * Replace MOCK_REVENUE with real data from GET /api/statistics/revenue
 * (reads serviceFee only, materialCost excluded per 3.4.S3) once ready.
 */

const MOCK_STATS = {
  revenueThisMonth: "18,400,000",
  revenueMonthDelta: "12.5% vs last month",
  revenueThisYear: "214,850,000",
  revenueYearDelta: "8.7% vs last year",
  completedBookings: 142,
};

const REVENUE_BY_SERVICE = {
  monthly: [
    { name: "Electrical Repair", pct: 38, amount: "7.0M", color: "#5b5fc7" },
    { name: "Plumbing Repair", pct: 29, amount: "5.3M", color: "#7c80d6" },
    { name: "Aircon Repair", pct: 21, amount: "3.9M", color: "#a4a7e5" },
    { name: "Roof Repair", pct: 12, amount: "2.2M", color: "#c8caf0" },
  ],
  yearly: [
    { name: "Electrical Repair", pct: 38, amount: "81.6M", color: "#5b5fc7" },
    { name: "Plumbing Repair", pct: 29, amount: "62.3M", color: "#7c80d6" },
    { name: "Aircon Repair", pct: 21, amount: "45.1M", color: "#a4a7e5" },
    { name: "Roof Repair", pct: 12, amount: "25.8M", color: "#c8caf0" },
  ],
};

const RECENT_RATINGS = [
  { bookingId: "B-0998", service: "Aircon Repair Service", rating: 4.6 },
];

export default function StatisticsPage() {
  const [period, setPeriod] = useState("monthly");
  const bars = REVENUE_BY_SERVICE[period];

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4">
        <h1 className="text-lg font-extrabold text-gray-900">Statistics</h1>
        <p className="text-xs text-gray-400 mt-0.5">Revenue Overview</p>
      </div>

      <div className="p-8 flex flex-col gap-5 max-w-4xl">
        {/* Stat cards */}
        <div className="flex gap-4.5 gap-4">
          <div className="flex-1 bg-white rounded-xl shadow-sm px-5 py-4.5">
            <div className="text-[11px] font-bold uppercase tracking-wide text-gray-400">
              Revenue This Month
            </div>
            <div className="text-2xl font-extrabold mt-2">{MOCK_STATS.revenueThisMonth} ₫</div>
            <div className="text-[11.5px] font-semibold text-green-600 mt-1">
              ↑ {MOCK_STATS.revenueMonthDelta}
            </div>
          </div>
          <div className="flex-1 bg-white rounded-xl shadow-sm px-5 py-4.5">
            <div className="text-[11px] font-bold uppercase tracking-wide text-gray-400">
              Revenue This Year
            </div>
            <div className="text-2xl font-extrabold mt-2">{MOCK_STATS.revenueThisYear} ₫</div>
            <div className="text-[11.5px] font-semibold text-green-600 mt-1">
              ↑ {MOCK_STATS.revenueYearDelta}
            </div>
          </div>
          <div className="flex-1 bg-white rounded-xl shadow-sm px-5 py-4.5">
            <div className="text-[11px] font-bold uppercase tracking-wide text-gray-400">
              Completed Bookings
            </div>
            <div className="text-2xl font-extrabold mt-2">{MOCK_STATS.completedBookings}</div>
            <div className="text-[11.5px] text-gray-400 mt-1">this month</div>
          </div>
        </div>

        {/* Revenue by service type */}
        <div className="bg-white rounded-xl shadow-sm px-6 py-5.5 p-6">
          <div className="flex items-center justify-between mb-4.5 mb-4">
            <div className="text-[11px] font-bold uppercase tracking-wide text-gray-400">
              Revenue by Service Type
            </div>
            <div className="flex gap-3.5 text-sm">
              <button
                onClick={() => setPeriod("monthly")}
                className={`font-bold ${
                  period === "monthly" ? "text-gray-900 underline" : "text-gray-400"
                }`}
              >
                Monthly
              </button>
              <button
                onClick={() => setPeriod("yearly")}
                className={`font-bold ${
                  period === "yearly" ? "text-gray-900 underline" : "text-gray-400"
                }`}
              >
                Yearly
              </button>
            </div>
          </div>

          {bars.map((b) => (
            <div key={b.name} className="flex items-center gap-3.5 mb-3.5">
              <span className="w-32 shrink-0 text-[12.5px] font-semibold">{b.name}</span>
              <div className="flex-1 h-5.5 h-6 bg-gray-100 rounded-md overflow-hidden">
                <div
                  className="h-full rounded-md"
                  style={{ width: `${b.pct}%`, backgroundColor: b.color }}
                />
              </div>
              <span className="w-16 text-right text-xs font-bold text-gray-500">
                {b.pct}% · {b.amount}
              </span>
            </div>
          ))}

          <div className="text-[11px] text-gray-400 mt-1.5">
            * Revenue tính theo Service Fee, không bao gồm Material Cost
          </div>
        </div>

        {/* Ratings entry point (3.4.S2) */}
        <div className="bg-white rounded-xl shadow-sm px-6 py-5">
          <div className="text-[11px] font-bold uppercase tracking-wide text-gray-400 mb-3">
            Recent Ratings
          </div>
          {RECENT_RATINGS.map((r) => (
            <Link
              key={r.bookingId}
              to={`/statistics/rating/${r.bookingId}`}
              className="flex items-center justify-between text-sm py-2 hover:bg-gray-50 rounded-lg px-2 -mx-2"
            >
              <span>
                #{r.bookingId} — {r.service}
              </span>
              <span className="font-bold text-indigo-600">⭐ {r.rating} →</span>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}
