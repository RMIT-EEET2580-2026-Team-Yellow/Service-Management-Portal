import StatusBadge from "./StatusBadge";

/**
 * Left column of BookingListPage — tabs, search/filter bar, and the booking
 * table. All state still lives in the parent; this component only renders
 * and calls the setters passed in as props.
 */
export default function BookingTable({
  tab,
  switchTab,
  searchText,
  setSearchText,
  serviceTypeFilter,
  setServiceTypeFilter,
  statusFilter,
  setStatusFilter,
  dateRangeFilter,
  setDateRangeFilter,
  sortOrder,
  setSortOrder,
  list,
  filteredList,
  selectedId,
  setSelectedId,
}) {
  return (
    <div className="flex-1 min-w-0">
      <div className="flex gap-6 border-b border-gray-200 mb-4">
        <button
          onClick={() => switchTab("active")}
          className={`pb-3 text-sm font-bold ${
            tab === "active" ? "text-gray-900 border-b-2 border-indigo-600" : "text-gray-400"
          }`}
        >
          Active Bookings
        </button>
        <button
          onClick={() => switchTab("past")}
          className={`pb-3 text-sm font-bold ${
            tab === "past" ? "text-gray-900 border-b-2 border-indigo-600" : "text-gray-400"
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
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="text-sm font-bold text-indigo-600 bg-white border border-gray-200 rounded-lg px-4 py-2.5 whitespace-nowrap cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Status: All</option>
            {[...new Set(list.map((b) => b.status))].map((s) => (
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
                  {searchText ? `No bookings match "${searchText}".` : "No bookings match the current filters."}
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
                  {b.technicians?.length ? (
                    b.technicians.map((t) => (
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
                        {b.technicians.length > 1 && t !== b.technicians[b.technicians.length - 1]
                          ? ", "
                          : ""}
                      </span>
                    ))
                  ) : (
                    <span className="text-gray-300">—</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
