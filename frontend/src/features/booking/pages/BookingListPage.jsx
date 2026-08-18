import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmBookingModal from "../components/ConfirmBookingModal";
import AssignTechnicianModal from "../components/AssignTechnicianModal";

/**
 * MOCK UI ONLY — no backend wired up yet.
 * Static data below stands in for GET /api/bookings + GET /api/bookings/{id}.
 * Replace MOCK_ACTIVE / MOCK_PAST with real API data via useBookings() once
 * the Booking backend (3.3.S1-S8, M1-M10) is implemented.
 */

const STATUS_STYLES = {
  Created: "bg-amber-100 text-amber-700",
  Confirmed: "bg-blue-100 text-blue-700",
  Scheduled: "bg-green-100 text-green-700",
  Working: "bg-red-100 text-red-700",
  Completed: "bg-indigo-100 text-indigo-700",
  "Cancelled by Customer": "bg-red-100 text-red-700",
  "Terminated by Customer": "bg-red-100 text-red-700",
  "Aborted by Technician": "bg-purple-100 text-purple-700",
};

function StatusBadge({ status }) {
  return (
    <span
      className={`text-xs font-semibold px-2.5 py-1 rounded-full whitespace-nowrap ${
        STATUS_STYLES[status] || "bg-gray-100 text-gray-700"
      }`}
    >
      {status}
    </span>
  );
}

const MOCK_ACTIVE = [
  {
    id: "#B-1042",
    status: "Created",
    service: "Aircon Repair Service",
    preferredDate: "20 Aug 2026",
    preferredDateISO: "2026-08-20",
    creationDate: "12 Aug 2026",
    creationDateISO: "2026-08-12",
    address: "—",
    issueDescription: "—",
    availableWindow: "08:00 – 12:00",
    suggestedFee: 400000,
    suggestedDuration: 2,
  },
  {
    id: "#B-1039",
    status: "Confirmed",
    service: "Plumbing Repair Service",
    preferredDate: "18 Aug 2026",
    preferredDateISO: "2026-08-18",
    creationDate: "10 Aug 2026",
    creationDateISO: "2026-08-10",
    address: "—",
    issueDescription: "—",
    serviceFee: "300,000 VND",
    estDuration: "1.5 hours",
    availableTechnicians: [
      { name: "Nguyễn Văn A", freeWindow: "Free 08:00–17:00" },
      { name: "Trần Thị B", freeWindow: "Free 09:00–15:00" },
    ],
    unavailableTechnicians: [{ name: "Lê Văn C", busyWith: "#B-1020" }],
  },
  {
    id: "#B-1031",
    status: "Scheduled",
    service: "Electrical Repair",
    preferredDate: "15 Aug 2026",
    preferredDateISO: "2026-08-15",
    creationDate: "08 Aug 2026",
    creationDateISO: "2026-08-08",
    address: "22 Le Loi, HCMC",
    issueDescription: "Circuit breaker trips repeatedly in kitchen area.",
    serviceFee: "350,000 VND",
    estDuration: "2 hours",
    scheduledTime: "15 Aug 2026, 09:00–11:00",
    technicians: [
      { name: "Nguyễn Văn A", response: "Accepted" },
      { name: "Trần Thị B", response: "Accepted" },
    ],
  },
  {
    id: "#B-1020",
    status: "Working",
    service: "Roof Repair Service",
    preferredDate: "12 Aug 2026",
    preferredDateISO: "2026-08-12",
    creationDate: "05 Aug 2026",
    creationDateISO: "2026-08-05",
    address: "14 Nguyen Hue, HCMC",
    issueDescription: "Roof tiles cracked after storm, minor water leak into attic.",
    serviceFee: "350,000 VND",
    estDuration: "2 hours",
    scheduledTime: "12 Aug 2026, 08:00–10:00",
    technicians: [{ name: "Lê Văn D", response: "Accepted" }],
  },
  {
    id: "#B-1017",
    status: "Confirmed",
    service: "Aircon Repair Service",
    preferredDate: "11 Aug 2026",
    preferredDateISO: "2026-08-11",
    creationDate: "04 Aug 2026",
    creationDateISO: "2026-08-04",
    address: "—",
    issueDescription: "—",
    serviceFee: "400,000 VND",
    estDuration: "2 hours",
    availableTechnicians: [
      { name: "Nguyễn Văn A", freeWindow: "Free 07:00–16:00" },
      { name: "Lê Văn D", freeWindow: "Free 10:00–18:00" },
    ],
    unavailableTechnicians: [{ name: "Trần Thị B", busyWith: "#B-1031" }],
  },
  {
    id: "#B-1009",
    status: "Created",
    service: "Plumbing Repair Service",
    preferredDate: "09 Aug 2026",
    preferredDateISO: "2026-08-09",
    creationDate: "02 Aug 2026",
    creationDateISO: "2026-08-02",
    address: "—",
    issueDescription: "—",
    availableWindow: "13:00 – 17:00",
    suggestedFee: 250000,
    suggestedDuration: 1.5,
  },
];

const MOCK_PAST = [
  {
    id: "#B-0998",
    status: "Completed",
    service: "Aircon Repair Service",
    preferredDate: "05 Aug 2026",
    preferredDateISO: "2026-08-05",
    creationDate: "01 Aug 2026",
    creationDateISO: "2026-08-01",
    completedDate: "05 Aug 2026",
    serviceFeeAmt: "350,000",
    materialCost: "120,000",
    totalCost: "470,000",
  },
  {
    id: "#B-0991",
    status: "Cancelled by Customer",
    service: "Plumbing Repair",
    preferredDate: "03 Aug 2026",
    preferredDateISO: "2026-08-03",
    creationDate: "30 Jul 2026",
    creationDateISO: "2026-07-30",
    reason: "Customer rescheduled to next week due to travel.",
  },
  {
    id: "#B-0985",
    status: "Aborted by Technician",
    service: "Roof Repair",
    preferredDate: "01 Aug 2026",
    preferredDateISO: "2026-08-01",
    creationDate: "28 Jul 2026",
    creationDateISO: "2026-07-28",
    reason: "Missing replacement roof tiles, unable to source on-site.",
  },
];

function matchesSearch(booking, query) {
  if (!query.trim()) return true;
  const q = query.trim().toLowerCase();
  if (booking.id.toLowerCase().includes(q)) return true;
  if (booking.service.toLowerCase().includes(q)) return true;
  if (booking.technicians?.some((t) => t.name.toLowerCase().includes(q))) return true;
  return false;
}

function matchesDateRange(booking, range) {
  if (!range) return true;
  const days = { last7: 7, last30: 30 }[range];
  if (!days) return true;
  const cutoff = new Date();
  cutoff.setDate(cutoff.getDate() - days);
  return new Date(booking.preferredDateISO) >= cutoff;
}

export default function BookingListPage() {
  const navigate = useNavigate();
  const [tab, setTab] = useState("active");
  const [searchText, setSearchText] = useState("");
  const [confirmingBooking, setConfirmingBooking] = useState(null);
  const [assigningBooking, setAssigningBooking] = useState(null);
  const [serviceTypeFilter, setServiceTypeFilter] = useState("");
  const [dateRangeFilter, setDateRangeFilter] = useState("");
  const [sortOrder, setSortOrder] = useState("newest");
  const [activeBookings, setActiveBookings] = useState(MOCK_ACTIVE);

  const list = tab === "active" ? activeBookings : MOCK_PAST;

  const filteredList = list
    .filter((b) => matchesSearch(b, searchText))
    .filter((b) => !serviceTypeFilter || b.service === serviceTypeFilter)
    .filter((b) => matchesDateRange(b, dateRangeFilter))
    .sort((a, b) =>
      sortOrder === "newest"
        ? b.creationDateISO.localeCompare(a.creationDateISO)
        : a.creationDateISO.localeCompare(b.creationDateISO)
    );

  const [selectedId, setSelectedId] = useState(MOCK_ACTIVE[2].id);
  const selected =
    filteredList.find((b) => b.id === selectedId) || filteredList[0] || list[0];

  function switchTab(next) {
    setTab(next);
    setSearchText("");
    setServiceTypeFilter("");
    setDateRangeFilter("");
    setSortOrder("newest");
    setSelectedId(next === "active" ? MOCK_ACTIVE[0].id : MOCK_PAST[0].id);
  }

  return (
    <div className="bg-gray-50 min-h-screen relative">
      {/* Page header */}
      <div className="bg-white border-b border-gray-200 px-8 py-4 flex items-center justify-between">
        <div>
          <h1 className="text-lg font-extrabold text-gray-900">Service Bookings</h1>
          <p className="text-xs text-gray-400 mt-0.5">
            Bookings / {tab === "active" ? "Active" : "Past"}
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate("/bookings/create")}
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold px-4 py-2.5 rounded-lg whitespace-nowrap"
          >
            + Create Booking
          </button>
          <div className="w-9 h-9 rounded-full bg-indigo-600 text-white flex items-center justify-center text-xs font-bold">
            TH
          </div>
        </div>
      </div>

      <div className="flex gap-6 p-8">
        {/* List column */}
        <div className="flex-1 min-w-0">
          <div className="flex gap-6 border-b border-gray-200 mb-4">
            <button
              onClick={() => switchTab("active")}
              className={`pb-3 text-sm font-bold ${
                tab === "active"
                  ? "text-gray-900 border-b-2 border-indigo-600"
                  : "text-gray-400"
              }`}
            >
              Active Bookings
            </button>
            <button
              onClick={() => switchTab("past")}
              className={`pb-3 text-sm font-bold ${
                tab === "past"
                  ? "text-gray-900 border-b-2 border-indigo-600"
                  : "text-gray-400"
              }`}
            >
              Past Bookings
            </button>
          </div>

          {tab === "active" && (
            <div className="flex gap-2.5 mb-4">
              <input
                placeholder="🔍 Search by Booking ID or Technician name…"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                className="flex-1 text-sm border border-gray-200 rounded-lg px-3 py-2.5 text-gray-700 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <select
                value={serviceTypeFilter}
                onChange={(e) => setServiceTypeFilter(e.target.value)}
                className="text-sm font-bold text-indigo-600 bg-white border border-gray-200 rounded-lg px-4 py-2.5 whitespace-nowrap cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="">Service Type: All</option>
                {[...new Set(list.map((b) => b.service))].map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>

              <select
                value={dateRangeFilter}
                onChange={(e) => setDateRangeFilter(e.target.value)}
                className="text-sm font-bold text-indigo-600 bg-white border border-gray-200 rounded-lg px-4 py-2.5 whitespace-nowrap cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="">Date Range: All</option>
                <option value="last7">Last 7 days</option>
                <option value="last30">Last 30 days</option>
              </select>

              <select
                value={sortOrder}
                onChange={(e) => setSortOrder(e.target.value)}
                className="text-sm font-bold text-indigo-600 bg-white border border-gray-200 rounded-lg px-4 py-2.5 whitespace-nowrap cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="newest">Sort: Newest</option>
                <option value="oldest">Sort: Oldest</option>
              </select>
            </div>
          )}

          <div className="bg-white rounded-xl shadow-sm overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-gray-50 text-[11px] uppercase tracking-wide text-gray-400">
                  <th className="text-left font-bold px-4 py-3">Booking ID</th>
                  <th className="text-left font-bold px-4 py-3">Status</th>
                  <th className="text-left font-bold px-4 py-3">Requested Service</th>
                  <th className="text-left font-bold px-4 py-3">Preferred Date</th>
                  <th className="text-left font-bold px-4 py-3">Creation Date</th>
                  <th className="text-left font-bold px-4 py-3">Technician</th>
                </tr>
              </thead>
              <tbody>
                {filteredList.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-sm text-gray-400">
                      No bookings match &quot;{searchText}&quot;.
                    </td>
                  </tr>
                )}
                {filteredList.map((b) => (
                  <tr
                    key={b.id}
                    onClick={() => setSelectedId(b.id)}
                    className={`border-t border-gray-100 cursor-pointer hover:bg-gray-50 ${
                      b.id === selectedId ? "bg-indigo-50" : ""
                    }`}
                  >
                    <td className="px-4 py-3.5 font-bold text-gray-900">{b.id}</td>
                    <td className="px-4 py-3.5">
                      <StatusBadge status={b.status} />
                    </td>
                    <td className="px-4 py-3.5">{b.service}</td>
                    <td className="px-4 py-3.5 text-gray-400 text-xs">{b.preferredDate}</td>
                    <td className="px-4 py-3.5 text-gray-400 text-xs">{b.creationDate}</td>
                    <td className="px-4 py-3.5 text-xs">
                      {b.technicians?.length
                        ? b.technicians.map((t) => (
                            <span
                              key={t.name}
                              className={
                                searchText.trim() &&
                                t.name.toLowerCase().includes(searchText.trim().toLowerCase())
                                  ? "bg-amber-100 px-1 rounded font-semibold text-gray-900"
                                  : ""
                              }
                            >
                              {t.name}
                              {b.technicians.length > 1 &&
                              t !== b.technicians[b.technicians.length - 1]
                                ? ", "
                                : ""}
                            </span>
                          ))
                        : <span className="text-gray-300">—</span>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Detail column */}
        <div className="w-[400px] shrink-0 bg-white rounded-xl shadow-sm border-2 border-indigo-600 flex flex-col overflow-hidden">
          <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
            <span className="font-extrabold text-sm">Booking {selected.id}</span>
            <StatusBadge status={selected.status} />
          </div>

          <div className="px-5 py-4 border-b border-gray-100">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">
              Booking Info
            </div>
            <div className="grid grid-cols-2 gap-3 text-xs">
              <div>
                <span className="text-gray-400 block text-[10px] mb-0.5">Service</span>
                <b>{selected.service}</b>
              </div>
              <div>
                <span className="text-gray-400 block text-[10px] mb-0.5">Preferred Date</span>
                <b>{selected.preferredDate}</b>
              </div>
              <div>
                <span className="text-gray-400 block text-[10px] mb-0.5">Creation Date</span>
                <b>{selected.creationDate}</b>
              </div>
              {selected.address && (
                <div>
                  <span className="text-gray-400 block text-[10px] mb-0.5">Address</span>
                  <b>{selected.address}</b>
                </div>
              )}
            </div>
            {selected.issueDescription && (
              <div className="mt-3 text-xs leading-relaxed">
                <span className="text-gray-400 block text-[10px] mb-0.5">Issue Description</span>
                {selected.issueDescription}
              </div>
            )}
          </div>

          {/* Completed cost summary */}
          {selected.status === "Completed" && (
            <div className="px-5 py-4 border-b border-gray-100 bg-gray-50">
              <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">
                Cost Summary
              </div>
              <div className="grid grid-cols-3 gap-2.5 text-center">
                <div className="bg-white rounded-lg p-3">
                  <span className="block text-[10px] text-gray-400 mb-1">Service Fee</span>
                  <b className="text-sm">{selected.serviceFeeAmt}</b>
                </div>
                <div className="bg-white rounded-lg p-3">
                  <span className="block text-[10px] text-gray-400 mb-1">Material Cost</span>
                  <b className="text-sm">{selected.materialCost}</b>
                </div>
                <div className="bg-indigo-900 rounded-lg p-3">
                  <span className="block text-[10px] text-indigo-200 mb-1">Total Cost</span>
                  <b className="text-sm text-white">{selected.totalCost}</b>
                </div>
              </div>
            </div>
          )}

          {/* Cancelled / Aborted reason */}
          {(selected.status === "Cancelled by Customer" ||
            selected.status === "Terminated by Customer" ||
            selected.status === "Aborted by Technician") && (
            <div className="px-5 py-4 border-b border-gray-100 bg-gray-50">
              <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">Reason</div>
              <p className="text-xs leading-relaxed">{selected.reason}</p>
            </div>
          )}

          {/* Confirmation + Assigned technicians */}
          {selected.serviceFee && (
            <div className="px-5 py-4 border-b border-gray-100 bg-gray-50">
              <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">
                Confirmation
              </div>
              <div className="grid grid-cols-2 gap-3 text-xs">
                <div>
                  <span className="text-gray-400 block text-[10px] mb-0.5">Service Fee</span>
                  <b>{selected.serviceFee}</b>
                </div>
                <div>
                  <span className="text-gray-400 block text-[10px] mb-0.5">Est. Duration</span>
                  <b>{selected.estDuration}</b>
                </div>
              </div>
            </div>
          )}

          {selected.technicians && (
            <div className="px-5 py-4 border-b border-gray-100">
              <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">
                Schedule &amp; Technician
              </div>
              {selected.scheduledTime && (
                <div className="mb-2.5 text-xs">
                  <span className="text-gray-400 block text-[10px] mb-0.5">Scheduled Time</span>
                  <b>{selected.scheduledTime}</b>
                </div>
              )}
              {selected.technicians.map((t) => (
                <div
                  key={t.name}
                  className="flex justify-between items-center bg-gray-50 rounded-lg px-3 py-2.5 mb-1.5 text-xs"
                >
                  <span>👤 {t.name}</span>
                  <StatusBadge status={t.response} />
                </div>
              ))}
            </div>
          )}

          <div className="mt-auto px-5 py-4 flex justify-end gap-2.5">
            {tab === "active" && selected.status === "Created" && (
              <button
                onClick={() => setConfirmingBooking(selected)}
                className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold px-4 py-2 rounded-lg"
              >
                Confirm Booking
              </button>
            )}
            {tab === "active" && selected.status === "Confirmed" && (
              <button
                onClick={() => setAssigningBooking(selected)}
                className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold px-4 py-2 rounded-lg"
              >
                Assign Technician
              </button>
            )}
            {tab === "active" && (
              <button className="border border-red-500 text-red-500 text-sm font-bold px-4 py-2 rounded-lg hover:bg-red-50">
                Cancel Booking
              </button>
            )}
          </div>
        </div>
      </div>

      <ConfirmBookingModal
        booking={confirmingBooking}
        onCancel={() => setConfirmingBooking(null)}
        onConfirm={() => {
          // MOCK ONLY — real version calls PATCH /api/bookings/{id}/confirm
          setConfirmingBooking(null);
        }}
      />

      <AssignTechnicianModal
        booking={assigningBooking}
        onCancel={() => setAssigningBooking(null)}
        onAssign={(selectedNames) => {
          // MOCK ONLY — real version calls POST /api/bookings/{id}/assign,
          // which creates TechnicianAssignment records (response starts as
          // Pending until the Technician accepts/rejects — 3.3.M4).
          setActiveBookings((prev) =>
            prev.map((b) =>
              b.id === assigningBooking.id
                ? {
                    ...b,
                    technicians: selectedNames.map((name) => ({
                      name,
                      response: "Pending",
                    })),
                  }
                : b
            )
          );
          setAssigningBooking(null);
        }}
      />
    </div>
  );
}
