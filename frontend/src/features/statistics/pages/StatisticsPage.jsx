import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useStatistics } from "../../../common/hooks/useStatistics";
import { useBookings } from "../../../common/hooks/useBookings";

/** VND display — mirrors the local formatter DashboardPage already uses.
 *  (common/utils/formatCurrency.js exists but formats as "$0.00" and isn't
 *  used anywhere in the app — don't import it here.) */
function formatVnd(value) {
  if (value == null) return "—";
  return `${Number(value).toLocaleString("en-US")} ₫`;
}

function StatCard({ label, value, hint }) {
  return (
    <div className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900 sm:p-5">
      <div className="text-[11px] font-bold uppercase tracking-wide text-gray-400 dark:text-slate-500">
        {label}
      </div>
      <div className="mt-2 text-xl font-extrabold text-gray-900 dark:text-white sm:text-2xl">
        {value}
      </div>
      {hint && <div className="mt-1 text-xs text-gray-400 dark:text-slate-500">{hint}</div>}
    </div>
  );
}

function RevenueByServiceType({ revenue }) {
  const shares = revenue?.revenueShareByServiceType ?? {};
  const amounts = revenue?.revenueByServiceType ?? {};
  const entries = Object.entries(shares).sort((a, b) => b[1] - a[1]);

  return (
    <div className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900 sm:p-6">
      <div className="mb-4 text-[11px] font-bold uppercase tracking-wide text-gray-400 dark:text-slate-500">
        Revenue by Service Type (Year to Date)
      </div>

      {entries.length === 0 && (
        <p className="text-sm text-gray-400 dark:text-slate-500">No revenue recorded yet this year.</p>
      )}

      {entries.map(([name, pct]) => (
        <div key={name} className="mb-3.5 flex flex-col gap-1.5 sm:flex-row sm:items-center sm:gap-3.5">
          <span className="text-[12.5px] font-semibold text-gray-700 dark:text-slate-300 sm:w-36 sm:shrink-0">
            {name}
          </span>
          <div className="h-5 flex-1 overflow-hidden rounded-md bg-gray-100 dark:bg-slate-800">
            <div
              className="h-full rounded-md bg-indigo-500"
              style={{ width: `${Math.min(pct, 100)}%` }}
            />
          </div>
          <span className="text-xs font-bold text-gray-500 dark:text-slate-400 sm:w-28 sm:text-right">
            {pct.toFixed(1)}% · {formatVnd(amounts[name])}
          </span>
        </div>
      ))}

      <div className="mt-1.5 text-[11px] text-gray-400 dark:text-slate-500">
        * Revenue calculated by Service Fee, Material Cost not included
      </div>
    </div>
  );
}

function RatingRow({ subjectId, subjectType, averageOverall, averageIntegrity, averageQuality, averageFriendliness }) {
  return (
    <>
      {/* Desktop/tablet row */}
      <tr className="hidden border-b border-gray-100 last:border-0 dark:border-slate-800 md:table-row">
        <td className="py-2.5 pr-3 text-sm font-semibold text-gray-800 dark:text-slate-200">{subjectId}</td>
        <td className="py-2.5 pr-3 text-center text-sm font-bold text-indigo-600 dark:text-indigo-400">
          ⭐ {averageOverall.toFixed(1)}
        </td>
        <td className="py-2.5 pr-3 text-center text-xs text-gray-500 dark:text-slate-400">{averageIntegrity.toFixed(1)}</td>
        <td className="py-2.5 pr-3 text-center text-xs text-gray-500 dark:text-slate-400">{averageQuality.toFixed(1)}</td>
        <td className="py-2.5 text-center text-xs text-gray-500 dark:text-slate-400">{averageFriendliness.toFixed(1)}</td>
      </tr>

      {/* Mobile card */}
      <tr className="table-row border-b border-gray-100 last:border-0 dark:border-slate-800 md:hidden">
        <td colSpan={5} className="py-3">
          <div className="flex items-center justify-between">
            <span className="text-sm font-semibold text-gray-800 dark:text-slate-200">{subjectId}</span>
            <span className="text-sm font-bold text-indigo-600 dark:text-indigo-400">
              ⭐ {averageOverall.toFixed(1)}
            </span>
          </div>
          <div className="mt-1.5 flex gap-4 text-[11px] text-gray-400 dark:text-slate-500">
            <span>Integrity {averageIntegrity.toFixed(1)}</span>
            <span>Quality {averageQuality.toFixed(1)}</span>
            <span>Friendliness {averageFriendliness.toFixed(1)}</span>
          </div>
        </td>
      </tr>
      {/* subjectType kept out of the visible row — every row in a given table already shares
          the same subjectType (SERVICE_TYPE or TECHNICIAN), so it'd be redundant per-row noise. */}
      {subjectType && false}
    </>
  );
}

function RatingsTable({ title, rows, loading, error, emptyHint }) {
  return (
    <div>
      <div className="mb-2.5 text-[11px] font-bold uppercase tracking-wide text-gray-400 dark:text-slate-500">
        {title}
      </div>
      {loading && <p className="text-sm text-gray-400 dark:text-slate-500">Loading…</p>}
      {error && <p className="text-sm text-red-500">{error}</p>}
      {!loading && !error && rows.length === 0 && (
        <p className="text-sm text-gray-400 dark:text-slate-500">{emptyHint}</p>
      )}
      {!loading && !error && rows.length > 0 && (
        <table className="w-full">
          <thead className="hidden md:table-header-group">
            <tr className="border-b border-gray-200 text-left text-[10px] font-bold uppercase text-gray-400 dark:border-slate-800 dark:text-slate-500">
              <th className="pb-2 pr-3 font-bold">Subject</th>
              <th className="pb-2 pr-3 text-center font-bold">Overall</th>
              <th className="pb-2 pr-3 text-center font-bold">Integrity</th>
              <th className="pb-2 pr-3 text-center font-bold">Quality</th>
              <th className="pb-2 text-center font-bold">Friendliness</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <RatingRow key={`${r.subjectType}-${r.subjectId}`} {...r} />
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default function StatisticsPage() {
  const {
    revenue,
    revenueLoading,
    revenueError,
    loadRevenue,
    serviceTypeRatings,
    serviceTypeRatingsLoading,
    serviceTypeRatingsError,
    loadServiceTypeRatings,
    technicianRatings,
    technicianRatingsLoading,
    technicianRatingsError,
    loadTechnicianRatings,
  } = useStatistics();

  const { past, listLoading, loadPast } = useBookings();

  const [ratingPeriod, setRatingPeriod] = useState("monthly");

  useEffect(() => {
    loadRevenue();
    loadPast();
  }, [loadRevenue, loadPast]);

  useEffect(() => {
    loadServiceTypeRatings(ratingPeriod);
    loadTechnicianRatings(ratingPeriod);
  }, [ratingPeriod, loadServiceTypeRatings, loadTechnicianRatings]);

  const completedBookings = (past ?? []).filter((b) => b.status === "COMPLETED");

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-slate-950">
      <div className="border-b border-gray-200 bg-white px-4 py-4 dark:border-slate-800 dark:bg-slate-900 sm:px-6 lg:px-8">
        <h1 className="text-lg font-extrabold text-gray-900 dark:text-white">Statistics</h1>
        <p className="mt-0.5 text-xs text-gray-400 dark:text-slate-500">Revenue & Rating Overview</p>
      </div>

      <div className="flex flex-col gap-5 p-4 sm:p-6 lg:max-w-6xl lg:p-8">
        {revenueError && (
          <div className="rounded-lg bg-red-50 px-4 py-2.5 text-sm text-red-600 dark:bg-red-950/40 dark:text-red-400">
            {revenueError}
          </div>
        )}

        {/* Stat cards — 1 col mobile, 3 cols tablet+ */}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <StatCard
            label="Revenue This Month"
            value={revenueLoading ? "…" : formatVnd(revenue?.currentMonthRevenue)}
          />
          <StatCard
            label="Revenue This Year"
            value={revenueLoading ? "…" : formatVnd(revenue?.currentYearRevenue)}
          />
          <StatCard
            label="Completed Bookings"
            value={listLoading ? "…" : completedBookings.length}
            hint="all time (loaded)"
          />
        </div>

        <RevenueByServiceType revenue={revenue} />

        {/* Ratings — 3.4.M3/U1/U2 */}
        <div className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900 sm:p-6">
          <div className="mb-4 flex items-center justify-between">
            <div className="text-[11px] font-bold uppercase tracking-wide text-gray-400 dark:text-slate-500">
              Ratings
            </div>
            <div className="flex gap-3 text-sm">
              {["monthly", "yearly"].map((p) => (
                <button
                  key={p}
                  onClick={() => setRatingPeriod(p)}
                  className={`font-bold capitalize ${
                    ratingPeriod === p
                      ? "text-gray-900 underline dark:text-white"
                      : "text-gray-400 dark:text-slate-600"
                  }`}
                >
                  {p}
                </button>
              ))}
            </div>
          </div>

          <div className="flex flex-col gap-6 lg:flex-row">
            <div className="flex-1">
              <RatingsTable
                title="By Service Type"
                rows={serviceTypeRatings}
                loading={serviceTypeRatingsLoading}
                error={serviceTypeRatingsError}
                emptyHint="No service-type ratings for this period."
              />
            </div>
            <div className="flex-1">
              <RatingsTable
                title="By Technician"
                rows={technicianRatings}
                loading={technicianRatingsLoading}
                error={technicianRatingsError}
                emptyHint="No technician ratings for this period."
              />
            </div>
          </div>
        </div>

        {/* Completed bookings — entry point into per-booking review (3.4.S2) */}
        <div className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900 sm:p-6">
          <div className="mb-3 text-[11px] font-bold uppercase tracking-wide text-gray-400 dark:text-slate-500">
            Completed Bookings
          </div>
          {listLoading && <p className="text-sm text-gray-400 dark:text-slate-500">Loading…</p>}
          {!listLoading && completedBookings.length === 0 && (
            <p className="text-sm text-gray-400 dark:text-slate-500">No completed bookings yet.</p>
          )}
          {completedBookings.map((b) => (
            <Link
              key={b.id}
              to={`/statistics/rating/${b.id}`}
              className="-mx-2 flex flex-col gap-0.5 rounded-lg px-2 py-2.5 text-sm hover:bg-gray-50 dark:hover:bg-slate-800 sm:flex-row sm:items-center sm:justify-between"
            >
              <span className="text-gray-700 dark:text-slate-300">
                #{b.id.slice(0, 8)}… — {b.serviceType}
              </span>
              <span className="text-xs font-semibold text-indigo-600 dark:text-indigo-400">
                View rating →
              </span>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}