import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmBookingModal from "../components/ConfirmBookingModal";
import AssignTechnicianModal from "../components/AssignTechnicianModal";
import CancelBookingModal from "../components/CancelBookingModal";
import BookingTable from "../components/BookingTable";
import BookingDetailPanel from "../components/BookingDetailPanel";
import { MOCK_ACTIVE, MOCK_PAST } from "../mockBookings";
import { matchesSearch, matchesDateRange } from "../bookingHelpers";

/**
 * MOCK UI ONLY — no backend wired up yet.
 * Mock data lives in mockBookings.js, pure filter helpers live in
 * bookingHelpers.js, JSX for the two columns lives in
 * components/BookingTable.jsx and components/BookingDetailPanel.jsx — this
 * file only owns the state.
 */
export default function BookingListPage() {
  const navigate = useNavigate();
  const [tab, setTab] = useState("active");
  const [searchText, setSearchText] = useState("");
  const [confirmingBooking, setConfirmingBooking] = useState(null);
  const [assigningBooking, setAssigningBooking] = useState(null);
  const [cancellingBooking, setCancellingBooking] = useState(null);
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
  const selected = filteredList.find((b) => b.id === selectedId) || filteredList[0] || list[0];

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
        <BookingTable
          tab={tab}
          switchTab={switchTab}
          searchText={searchText}
          setSearchText={setSearchText}
          serviceTypeFilter={serviceTypeFilter}
          setServiceTypeFilter={setServiceTypeFilter}
          dateRangeFilter={dateRangeFilter}
          setDateRangeFilter={setDateRangeFilter}
          sortOrder={sortOrder}
          setSortOrder={setSortOrder}
          list={list}
          filteredList={filteredList}
          selectedId={selectedId}
          setSelectedId={setSelectedId}
        />

        <BookingDetailPanel
          selected={selected}
          tab={tab}
          onConfirmBooking={() => setConfirmingBooking(selected)}
          onAssignTechnician={() => setAssigningBooking(selected)}
          onCancelBooking={() => setCancellingBooking(selected)}
        />
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

      <CancelBookingModal
        booking={cancellingBooking}
        onBack={() => setCancellingBooking(null)}
        onConfirm={(reason, targetStatus) => {
          // MOCK ONLY — real version calls POST /api/bookings/{id}/cancel
          // (creates a BookingCancellation record, same pattern as /assign).
          setActiveBookings((prev) =>
            prev.map((b) => (b.id === cancellingBooking.id ? { ...b, status: targetStatus, reason } : b))
          );
          setCancellingBooking(null);
        }}
      />
    </div>
  );
}
