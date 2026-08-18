import { useEffect, useRef, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import logo from "../../../assets/logo.png";

const navItems = [
  { icon: "grid", label: "Dashboard", path: "/dashboard" },
  { icon: "calendar", label: "Bookings", path: "/bookings" },
  { icon: "users", label: "Staff", path: "/staff" },
];

const metrics = [
  { label: "Total Bookings", value: "1,482", delta: "+12.5%", trend: "up" },
  { label: "Active Technicians", value: "28 / 32", delta: "+4.2%", trend: "up" },
  { label: "Revenue", value: "$42,850", delta: "+18.7%", trend: "up" },
  { label: "Pending Jobs", value: "19", delta: "-2.4%", trend: "down" },
];

const technicians = [
  { initials: "MV", name: "Marcus Vance", role: "Senior HVAC Specialist", status: "Active", color: "bg-emerald-500" },
  { initials: "SJ", name: "Sarah Jenkins", role: "Electrical Engineer", status: "On route", color: "bg-blue-500" },
  { initials: "DM", name: "David Miller", role: "Plumbing Master", status: "Away", color: "bg-amber-500" },
  { initials: "EZ", name: "Emily Zhao", role: "Appliance Technician", status: "Active", color: "bg-emerald-500" },
];

const bookings = [
  { id: "#BKG-2041", customer: "Robert Dowson", technician: "Marcus Vance", service: "Central AC Compressor Repair", status: "Working", date: "Oct 24, 2024" },
  { id: "#BKG-2040", customer: "Alice Cooper", technician: "Sarah Jenkins", service: "Breaker Panel Diagnostics", status: "Completed", date: "Oct 24, 2024" },
  { id: "#BKG-2039", customer: "Jonathan Wright", technician: "David Miller", service: "Bathroom Pipe Leaks", status: "Scheduled", date: "Oct 23, 2024" },
  { id: "#BKG-2038", customer: "Michael Chang", technician: "Emily Zhao", service: "Refrigerator Maintenance", status: "Cancelled", date: "Oct 22, 2024" },
];

const statusStyles = {
  Working: "bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-300",
  Completed: "bg-emerald-100 text-emerald-700 dark:bg-emerald-500/20 dark:text-emerald-300",
  Scheduled: "bg-amber-100 text-amber-700 dark:bg-amber-500/20 dark:text-amber-300",
  Cancelled: "bg-red-100 text-red-700 dark:bg-red-500/20 dark:text-red-300",
};

const chartPoints = "10,170 90,140 170,180 250,95 330,130 410,70 490,120 570,50";
const chartCircles = chartPoints.split(" ").map((p) => p.split(",").map(Number));
const chartDays = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Today"];

const bookingStatuses = ["All", "Working", "Completed", "Scheduled", "Cancelled"];

export default function DashboardPage() {
  const [isDark, setIsDark] = useState(
    () => document.documentElement.classList.contains("dark")
  );
  const [menuOpen, setMenuOpen] = useState(false);
  const [statusFilter, setStatusFilter] = useState("All");
  const [filterOpen, setFilterOpen] = useState(false);
  const menuRef = useRef(null);
  const filterRef = useRef(null);
  const navigate = useNavigate();

  const filteredBookings =
    statusFilter === "All" ? bookings : bookings.filter((b) => b.status === statusFilter);

  useEffect(() => {
    document.documentElement.classList.toggle("dark", isDark);
  }, [isDark]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
      if (filterRef.current && !filterRef.current.contains(event.target)) {
        setFilterOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="flex min-h-screen bg-slate-50 dark:bg-slate-950">
      <aside className="sticky top-0 flex h-screen w-16 flex-shrink-0 flex-col items-center gap-2 border-r border-slate-200 bg-slate-900 py-4 dark:border-slate-800">
        <img src={logo} alt="Fixinh" className="mb-4 h-9 w-9 rounded-lg object-contain" />
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex h-10 w-10 items-center justify-center rounded-lg text-lg transition-colors ${
                isActive
                  ? "bg-blue-600/20 text-blue-400"
                  : "text-slate-400 hover:bg-slate-800 hover:text-white"
              }`
            }
          >
            <NavIcon name={item.icon} />
          </NavLink>
        ))}

        <div ref={menuRef} className="relative mt-auto">
          {menuOpen && (
            <div className="absolute bottom-0 left-14 z-10 w-48 rounded-xl border border-slate-700 bg-slate-800 p-1.5 shadow-lg">
              <div className="px-2.5 py-2">
                <p className="text-sm font-medium text-white">Alex Rivera</p>
                <p className="text-xs text-slate-400">dinhlam2901@gmail.com</p>
              </div>
              <div className="my-1 h-px bg-slate-700" />
              <button
                onClick={() => setIsDark((v) => !v)}
                className="flex w-full items-center gap-2.5 rounded-lg px-2.5 py-2 text-left text-sm text-slate-200 hover:bg-slate-700"
              >
                <NavIcon name={isDark ? "sun" : "moon"} />
                {isDark ? "Switch to light mode" : "Switch to dark mode"}
              </button>
              <button
                onClick={() => {
                  setMenuOpen(false);
                  navigate("/");
                }}
                className="flex w-full items-center gap-2.5 rounded-lg px-2.5 py-2 text-left text-sm text-red-400 hover:bg-slate-700"
              >
                <NavIcon name="logout" />
                Log out
              </button>
            </div>
          )}
          <button
            aria-label="Account menu"
            onClick={() => setMenuOpen((v) => !v)}
            className={`flex h-9 w-9 items-center justify-center rounded-full text-xs font-semibold transition-colors ${
              menuOpen ? "bg-blue-500 text-white" : "bg-blue-100 text-blue-700 hover:bg-blue-200"
            }`}
          >
            AR
          </button>
        </div>
      </aside>

      <main className="flex-1 px-8 py-6">
        <header className="mb-6 flex flex-wrap items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 dark:text-white">Dashboard</h1>
            <p className="text-sm text-slate-500 dark:text-slate-400">
              Welcome back, Reception. Here is today&apos;s operations summary.
            </p>
          </div>
          <div className="flex items-center gap-3">
            <input
              type="text"
              placeholder="Search bookings, techs..."
              className="w-56 rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 placeholder-slate-400 focus:border-blue-400 focus:outline-none dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200"
            />
            <button
              aria-label="Notifications"
              className="relative flex h-9 w-9 items-center justify-center rounded-lg border border-slate-200 bg-white dark:border-slate-700 dark:bg-slate-900 dark:text-slate-300"
            >
              <NavIcon name="bell" />
              <span className="absolute right-2 top-2 h-1.5 w-1.5 rounded-full bg-red-500" />
            </button>
            <div className="flex items-center gap-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-xs font-semibold text-blue-700 dark:bg-blue-500/20 dark:text-blue-300">
                AR
              </div>
              <div className="leading-tight">
                <p className="text-sm font-semibold text-slate-900 dark:text-white">Alex Rivera</p>
                <p className="text-xs uppercase text-slate-400">Reception</p>
              </div>
            </div>
          </div>
        </header>

        <section className="mb-5 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4">
          {metrics.map((m) => (
            <div key={m.label} className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900">
              <p className="text-sm text-slate-500 dark:text-slate-400">{m.label}</p>
              <p className="mt-2 text-2xl font-bold text-slate-900 dark:text-white">{m.value}</p>
              <p
                className={`mt-1 text-xs font-medium ${
                  m.trend === "up" ? "text-emerald-600" : "text-red-600"
                }`}
              >
                {m.trend === "up" ? "↑" : "↓"} {m.delta} vs last month
              </p>
            </div>
          ))}
        </section>

        <section className="mb-5 grid grid-cols-1 gap-4 lg:grid-cols-3">
          <div className="rounded-xl bg-white p-5 shadow-sm dark:bg-slate-900 lg:col-span-2">
            <div className="mb-4 flex items-start justify-between">
              <div>
                <h2 className="text-base font-semibold text-slate-900 dark:text-white">Booking Trends Over Time</h2>
                <p className="text-xs text-slate-400">Daily successful schedules and dispatches</p>
              </div>
              <span className="flex items-center gap-1 text-xs text-slate-500 dark:text-slate-400">
                <span className="h-2 w-2 rounded-full bg-blue-500" /> Current period
              </span>
            </div>
            <svg viewBox="0 0 620 220" className="h-52 w-full">
              <polyline points={chartPoints} fill="none" stroke="#3b82f6" strokeWidth="2.5" />
              {chartCircles.map(([x, y]) => (
                <circle key={`${x}-${y}`} cx={x} cy={y} r="3.5" fill="#3b82f6" />
              ))}
              {chartDays.map((day, i) => (
                <text
                  key={day}
                  x={10 + i * 80}
                  y="212"
                  fontSize="11"
                  fill="#94a3b8"
                >
                  {day}
                </text>
              ))}
            </svg>
          </div>

          <div className="rounded-xl bg-white p-5 shadow-sm dark:bg-slate-900">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-base font-semibold text-slate-900 dark:text-white">Technician Availability</h2>
              <span className="text-xs font-medium text-blue-600 dark:text-blue-400">View all (32)</span>
            </div>
            <div className="flex flex-col gap-4">
              {technicians.map((t) => (
                <div key={t.name} className="flex items-center gap-3">
                  <div className="flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-full bg-blue-100 text-xs font-semibold text-blue-700 dark:bg-blue-500/20 dark:text-blue-300">
                    {t.initials}
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-medium text-slate-900 dark:text-white">{t.name}</p>
                    <p className="truncate text-xs text-slate-400">{t.role}</p>
                  </div>
                  <span className="flex items-center gap-1 text-xs text-slate-500 dark:text-slate-400">
                    <span className={`h-1.5 w-1.5 rounded-full ${t.color}`} />
                    {t.status}
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
                  <div className="absolute right-0 z-10 mt-1.5 w-40 rounded-xl border border-slate-200 bg-white p-1.5 shadow-lg dark:border-slate-700 dark:bg-slate-800">
                    {bookingStatuses.map((status) => (
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
              <button className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-50 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800">
                Export CSV
              </button>
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full min-w-[720px] table-fixed border-collapse text-sm">
              <thead>
                <tr className="border-b border-slate-100 text-left text-xs text-slate-400 dark:border-slate-800">
                  <th className="w-[14%] py-2 font-normal">Booking ID</th>
                  <th className="w-[18%] py-2 font-normal">Customer</th>
                  <th className="w-[16%] py-2 font-normal">Technician</th>
                  <th className="w-[26%] py-2 font-normal">Service type</th>
                  <th className="w-[14%] py-2 font-normal">Status</th>
                  <th className="w-[12%] py-2 text-right font-normal">Date</th>
                </tr>
              </thead>
              <tbody>
                {filteredBookings.map((b) => (
                  <tr key={b.id} className="border-b border-slate-50 last:border-0 dark:border-slate-800/60">
                    <td className="py-3 font-medium text-blue-600 dark:text-blue-400">{b.id}</td>
                    <td className="py-3 font-medium text-slate-900 dark:text-white">{b.customer}</td>
                    <td className="py-3 text-slate-500 dark:text-slate-400">{b.technician}</td>
                    <td className="py-3 text-slate-500 dark:text-slate-400">{b.service}</td>
                    <td className="py-3">
                      <span className={`rounded-full px-3 py-1 text-xs font-medium ${statusStyles[b.status]}`}>
                        {b.status}
                      </span>
                    </td>
                    <td className="py-3 text-right text-slate-400">{b.date}</td>
                  </tr>
                ))}
                {filteredBookings.length === 0 && (
                  <tr>
                    <td colSpan={6} className="py-6 text-center text-sm text-slate-400">
                      No bookings match this status.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  );
}

function NavIcon({ name, className = "h-5 w-5" }) {
  const paths = {
    grid: "M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z",
    calendar: "M4 5h16v16H4zM4 9h16M8 3v4M16 3v4",
    users: "M9 11a3 3 0 100-6 3 3 0 000 6zM17 11a3 3 0 100-6 3 3 0 000 6zM3 21v-1a5 5 0 015-5h2a5 5 0 015 5v1M14 15h1a5 5 0 015 5v1",
    bell: "M12 3a5 5 0 00-5 5v3.5c0 .8-.3 1.6-.9 2.2L4.5 15.5h15l-1.6-1.8a3 3 0 01-.9-2.2V8a5 5 0 00-5-5zM9.5 19a2.5 2.5 0 005 0",
    sun: "M12 4V2M12 22v-2M4 12H2M22 12h-2M5 5l-1.4-1.4M19 19l-1.4-1.4M5 19l-1.4 1.4M19 5l-1.4 1.4M12 17a5 5 0 100-10 5 5 0 000 10z",
    moon: "M20 14.5A8 8 0 019.5 4 8 8 0 1020 14.5z",
    logout: "M9 3H5a2 2 0 00-2 2v14a2 2 0 002 2h4M16 17l5-5-5-5M21 12H9",
    chevron: "M6 9l6 6 6-6",
    check: "M5 13l4 4L19 7",
  };
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <path d={paths[name]} />
    </svg>
  );
}
