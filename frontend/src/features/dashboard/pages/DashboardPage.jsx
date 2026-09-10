import { useEffect, useMemo, useRef, useState } from "react";
import { useSelector } from "react-redux";
import NavIcon from "../../../components/reusable/NavIcon";
import { useBookings } from "../../../common/hooks/useBookings";
import { fetchAllTechnicians } from "../../technician/services/technicianApi";
import { fetchRevenueStats } from "../../statistics/services/statisticsApi";
import { fetchStaffById } from "../../staff/services/staffApi";
import { STATUS_STYLES } from "../../booking/bookingHelpers";

/** Bookings not yet started — awaiting confirmation/scheduling (3.3.M2 status list). */
const PENDING_STATUSES = ["CREATED", "CONFIRMED"];

const AVATAR_COLORS = ["bg-blue-100 text-blue-700", "bg-emerald-100 text-emerald-700",
  "bg-amber-100 text-amber-700", "bg-purple-100 text-purple-700", "bg-rose-100 text-rose-700"];

function getInitials(name) {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/);
  const first = parts[0]?.[0] ?? "";
  const last = parts.length > 1 ? parts[parts.length - 1][0] : "";
  return (first + last).toUpperCase();
}

function formatCurrency(value) {
  if (value == null) return "—";
  return `${Number(value).toLocaleString("en-US")} ₫`;
}

function shortId(id, len = 8) {
  if (!id) return "—";
  return id.length > len ? `${id.slice(0, len)}…` : id;
}

function exportBookingsToCsv(bookings) {
  const headers = ["Booking ID", "Customer ID", "Technician(s)", "Service Type", "Status", "Date"];
  const rows = bookings.map((b) => [
    b.id,
    b.customerId,
    (b.assignedTechnicianNames ?? []).join("; "),
    b.serviceType,
    b.status,
    b.preferredDate,
  ]);
  const csv = [headers, ...rows]
    .map((row) => row.map((cell) => `"${String(cell ?? "").replace(/"/g, '""')}"`).join(","))
    .join("\n");
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = `bookings-${new Date().toISOString().slice(0, 10)}.csv`;
  link.click();
  URL.revokeObjectURL(url);
}

export default function DashboardPage() {
  const user = useSelector((state) => state.auth.user);
  const { active, past, listLoading, listError, loadActive, loadPast } = useBookings();

  const [technicians, setTechnicians] = useState([]);
  const [techLoading, setTechLoading] = useState(true);
  const [revenue, setRevenue] = useState(null);
  const [staffProfile, setStaffProfile] = useState(null);

  const [statusFilter, setStatusFilter] = useState("All");
  const [filterOpen, setFilterOpen] = useState(false);
  const [searchText, setSearchText] = useState("");
  const filterRef = useRef(null);

  useEffect(() => {
    loadActive();
    loadPast();
  }, [loadActive, loadPast]);

  useEffect(() => {
    let cancelled = false;
    fetchAllTechnicians()
      .then((res) => {
        if (!cancelled) setTechnicians(res.data ?? []);
      })
      .catch(() => {
        // ADMIN-only endpoint — a non-Admin viewer simply sees an empty roster.
      })
      .finally(() => {
        if (!cancelled) setTechLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    let cancelled = false;
    fetchRevenueStats()
      .then((res) => {
        if (!cancelled) setRevenue(res.data);
      })
      .catch(() => {
        // ADMIN-only endpoint — leave revenue unset for other roles.
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (!user?.staffId) return undefined;
    let cancelled = false;
    fetchStaffById(user.staffId)
      .then((res) => {
        if (!cancelled) setStaffProfile(res.data);
      })
      .catch(() => {
        // ADMIN-only endpoint — header falls back to the JWT's role/staffId below.
      });
    return () => {
      cancelled = true;
    };
  }, [user?.staffId]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (filterRef.current && !filterRef.current.contains(event.target)) {
        setFilterOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const allBookings = useMemo(() => {
    return [...(active ?? []), ...(past ?? [])].sort(
      (a, b) => new Date(b.createdAt ?? 0) - new Date(a.createdAt ?? 0)
    );
  }, [active, past]);

  const metrics = useMemo(() => {
    const pendingJobs = allBookings.filter((b) => PENDING_STATUSES.includes(b.status)).length;
    const availableTechs = technicians.filter((t) => t.currentlyAvailable).length;
    return [
      { label: "Total Bookings", value: String(allBookings.length) },
      { label: "Active Technicians", value: `${availableTechs} / ${technicians.length}` },
      { label: "Revenue this month", value: formatCurrency(revenue?.currentMonthRevenue) },
      { label: "Pending Jobs", value: String(pendingJobs) },
    ];
  }, [allBookings, technicians, revenue]);

  const trend = useMemo(() => {
    const days = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setHours(0, 0, 0, 0);
      d.setDate(d.getDate() - i);
      days.push(d);
    }
    const counts = days.map(
      (day) =>
        allBookings.filter((b) => b.createdAt && new Date(b.createdAt).toDateString() === day.toDateString())
          .length
    );
    const max = Math.max(1, ...counts);
    const width = 620;
    const height = 220;
    const padX = 20;
    const padTop = 20;
    const padBottom = 40;
    const usableHeight = height - padTop - padBottom;
    const points = counts.map((count, i) => {
      const x = padX + ((width - padX * 2) / (counts.length - 1)) * i;
      const y = padTop + usableHeight - (count / max) * usableHeight;
      return [x, y];
    });
    const labels = days.map((d, i) =>
      i === days.length - 1 ? "Today" : d.toLocaleDateString(undefined, { weekday: "short" })
    );
    return { points, labels, height };
  }, [allBookings]);

  const statusOptions = useMemo(() => {
    const present = new Set(allBookings.map((b) => b.status).filter(Boolean));
    return ["All", ...Array.from(present).sort()];
  }, [allBookings]);

  const filteredBookings = useMemo(() => {
    const needle = searchText.trim().toLowerCase();
    return allBookings
      .filter((b) => statusFilter === "All" || b.status === statusFilter)
      .filter((b) => {
        if (!needle) return true;
        const haystack = [b.id, b.customerId, b.serviceType, ...(b.assignedTechnicianNames ?? [])]
          .filter(Boolean)
          .join(" ")
          .toLowerCase();
        return haystack.includes(needle);
      })
      .slice(0, 8);
  }, [allBookings, statusFilter, searchText]);

  const headerName = staffProfile?.fullName ?? user?.staffId ?? "—";
  const headerRole = staffProfile?.role ?? user?.role ?? "";

  return (
    <div className="px-8 py-6">
      <header className="mb-6 flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">Dashboard</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">
            Welcome back, {headerName}. Here is today&apos;s operations summary.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <input
            type="text"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            placeholder="Search bookings, techs..."
            className="w-56 rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 placeholder-slate-400 focus:border-blue-400 focus:outline-none dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200"
          />
          <button
            aria-label="Notifications"
            className="relative flex h-9 w-9 items-center justify-center rounded-lg border border-slate-200 bg-white dark:border-slate-700 dark:bg-slate-900 dark:text-slate-300"
          >
            <NavIcon name="bell" />
          </button>
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-xs font-semibold text-blue-700 dark:bg-blue-500/20 dark:text-blue-300">
              {getInitials(headerName)}
            </div>
            <div className="leading-tight">
              <p className="text-sm font-semibold text-slate-900 dark:text-white">{headerName}</p>
              <p className="text-xs uppercase text-slate-400">{headerRole}</p>
            </div>
          </div>
        </div>
      </header>

      {listError && (
        <div className="mb-4 rounded-lg bg-red-50 px-4 py-2 text-sm text-red-700 dark:bg-red-500/10 dark:text-red-300">
          Failed to load bookings: {listError}
        </div>
      )}

      <section className="mb-5 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4">
        {metrics.map((m) => (
          <div key={m.label} className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900">
            <p className="text-sm text-slate-500 dark:text-slate-400">{m.label}</p>
            <p className="mt-2 text-2xl font-bold text-slate-900 dark:text-white">
              {listLoading ? "…" : m.value}
            </p>
          </div>
        ))}
      </section>

      <section className="mb-5 grid grid-cols-1 gap-4 lg:grid-cols-3">
        <div className="rounded-xl bg-white p-5 shadow-sm dark:bg-slate-900 lg:col-span-2">
          <div className="mb-4 flex items-start justify-between">
            <div>
              <h2 className="text-base font-semibold text-slate-900 dark:text-white">Booking Trends Over Time</h2>
              <p className="text-xs text-slate-400">Bookings created per day, last 7 days</p>
            </div>
            <span className="flex items-center gap-1 text-xs text-slate-500 dark:text-slate-400">
              <span className="h-2 w-2 rounded-full bg-blue-500" /> Current period
            </span>
          </div>
          <svg viewBox={`0 0 620 ${trend.height}`} className="h-52 w-full">
            <polyline
              points={trend.points.map(([x, y]) => `${x},${y}`).join(" ")}
              fill="none"
              stroke="#3b82f6"
              strokeWidth="2.5"
            />
            {trend.points.map(([x, y], i) => (
              <circle key={`${x}-${i}`} cx={x} cy={y} r="3.5" fill="#3b82f6" />
            ))}
            {trend.labels.map((day, i) => (
              <text key={day + i} x={trend.points[i][0] - 10} y={trend.height - 12} fontSize="11" fill="#94a3b8">
                {day}
              </text>
            ))}
          </svg>
        </div>

        <div className="rounded-xl bg-white p-5 shadow-sm dark:bg-slate-900">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-base font-semibold text-slate-900 dark:text-white">Technician Availability</h2>
            <span className="text-xs font-medium text-blue-600 dark:text-blue-400">
              {technicians.length ? `View all (${technicians.length})` : ""}
            </span>
          </div>
          <div className="flex flex-col gap-4">
            {techLoading && <p className="text-xs text-slate-400">Loading technicians…</p>}
            {!techLoading && technicians.length === 0 && (
              <p className="text-xs text-slate-400">No technician data available.</p>
            )}
            {technicians.slice(0, 5).map((t, i) => (
              <div key={t.id} className="flex items-center gap-3">
                <div
                  className={`flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-full text-xs font-semibold ${AVATAR_COLORS[i % AVATAR_COLORS.length]}`}
                >
                  {getInitials(t.fullName)}
                </div>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium text-slate-900 dark:text-white">{t.fullName}</p>
                  <p className="truncate text-xs text-slate-400">
                    {t.workingDays?.length ? t.workingDays.join(", ") : "No schedule set"}
                  </p>
                </div>
                <span className="flex items-center gap-1 text-xs text-slate-500 dark:text-slate-400">
                  <span className={`h-1.5 w-1.5 rounded-full ${t.currentlyAvailable ? "bg-emerald-500" : "bg-slate-400"}`} />
                  {t.currentlyAvailable ? "Available" : "Unavailable"}
                </span>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="rounded-xl bg-white p-5 shadow-sm dark:bg-slate-900">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-base font-semibold text-slate-900 dark:text-white">Recent Bookings</h2>
          <div className="flex gap-2">
            <div ref={filterRef} className="relative">
              <button
                onClick={() => setFilterOpen((v) => !v)}
                className={`flex items-center gap-1.5 rounded-lg border px-3 py-1.5 text-xs font-medium transition-colors ${
                  statusFilter !== "All"
                    ? "border-blue-200 bg-blue-50 text-blue-700 dark:border-blue-500/40 dark:bg-blue-500/10 dark:text-blue-300"
                    : "border-slate-200 text-slate-600 hover:bg-slate-50 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
                }`}
              >
                {statusFilter === "All" ? "Filter status" : statusFilter}
                <NavIcon name="chevron" className="h-3.5 w-3.5" />
              </button>
              {filterOpen && (
                <div className="absolute right-0 z-10 mt-1.5 w-48 rounded-xl border border-slate-200 bg-white p-1.5 shadow-lg dark:border-slate-700 dark:bg-slate-800">
                  {statusOptions.map((status) => (
                    <button
                      key={status}
                      onClick={() => {
                        setStatusFilter(status);
                        setFilterOpen(false);
                      }}
                      className={`flex w-full items-center justify-between rounded-lg px-2.5 py-1.5 text-left text-xs font-medium ${
                        statusFilter === status
                          ? "bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-300"
                          : "text-slate-600 hover:bg-slate-50 dark:text-slate-300 dark:hover:bg-slate-700"
                      }`}
                    >
                      {status}
                      {statusFilter === status && <NavIcon name="check" className="h-3.5 w-3.5" />}
                    </button>
                  ))}
                </div>
              )}
            </div>
            <button
              onClick={() => exportBookingsToCsv(filteredBookings)}
              disabled={filteredBookings.length === 0}
              className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
            >
              Export CSV
            </button>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full min-w-[720px] table-fixed border-collapse text-sm">
            <thead>
              <tr className="border-b border-slate-100 text-left text-xs text-slate-400 dark:border-slate-800">
                <th className="w-[14%] py-2 font-normal">Booking ID</th>
                <th className="w-[16%] py-2 font-normal">Customer ID</th>
                <th className="w-[18%] py-2 font-normal">Technician</th>
                <th className="w-[22%] py-2 font-normal">Service type</th>
                <th className="w-[18%] py-2 font-normal">Status</th>
                <th className="w-[12%] py-2 text-right font-normal">Date</th>
              </tr>
            </thead>
            <tbody>
              {filteredBookings.map((b) => (
                <tr key={b.id} className="border-b border-slate-50 last:border-0 dark:border-slate-800/60">
                  <td className="py-3 font-medium text-blue-600 dark:text-blue-400" title={b.id}>
                    #{shortId(b.id)}
                  </td>
                  <td className="py-3 text-slate-500 dark:text-slate-400" title={b.customerId}>
                    {shortId(b.customerId)}
                  </td>
                  <td className="py-3 text-slate-500 dark:text-slate-400">
                    {b.assignedTechnicianNames?.length ? b.assignedTechnicianNames.join(", ") : "Unassigned"}
                  </td>
                  <td className="py-3 text-slate-500 dark:text-slate-400">{b.serviceType}</td>
                  <td className="py-3">
                    <span
                      className={`rounded-full px-3 py-1 text-xs font-medium ${
                        STATUS_STYLES[b.status] ?? "bg-slate-100 text-slate-700"
                      }`}
                    >
                      {b.status}
                    </span>
                  </td>
                  <td className="py-3 text-right text-slate-400">{b.preferredDate}</td>
                </tr>
              ))}
              {filteredBookings.length === 0 && (
                <tr>
                  <td colSpan={6} className="py-6 text-center text-sm text-slate-400">
                    {listLoading ? "Loading bookings…" : "No bookings match this status."}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
