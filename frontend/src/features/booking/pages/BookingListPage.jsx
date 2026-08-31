import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmBookingModal from "../components/ConfirmBookingModal";
import AssignTechnicianModal from "../components/AssignTechnicianModal";
import CancelBookingModal from "../components/CancelBookingModal";
import BookingTable from "../components/BookingTable";
import BookingDetailPanel from "../components/BookingDetailPanel";
import {
  matchesSearch,
  toDisplayBooking,
  ACTIVE_STATUSES,
  dateRangeToFrom,
  sortOrderToParam,
} from "../bookingHelpers";
import { useBookings } from "../../../common/hooks/useBookings";

export default function BookingListPage() {
  const navigate = useNavigate();
  const {
    active,
    past,
    listLoading,
    listError,
    loadActive,
    loadPast,
    filtered,
    filterLoading,
    filterError,
    loadFiltered,
    confirm,
    confirming,
    confirmError,
  } = useBookings();
  const [tab, setTab] = useState("active");
  const [searchText, setSearchText] = useState("");
  const [confirmingBooking, setConfirmingBooking] = useState(null);
  const [assigningBooking, setAssigningBooking] = useState(null);
  const [cancellingBooking, setCancellingBooking] = useState(null);
  const [serviceTypeFilter, setServiceTypeFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [dateRangeFilter, setDateRangeFilter] = useState("");
  const [sortOrder, setSortOrder] = useState("newest");
  const [selectedId, setSelectedId] = useState(null);

  useEffect(() => {
    loadActive();
    loadPast();
  }, [loadActive, loadPast]);

  useEffect(() => {
    if (tab !== "active") return;
    loadFiltered({
      serviceType: serviceTypeFilter,
      from: dateRangeToFrom(dateRangeFilter),
      sort: sortOrderToParam(sortOrder),
    });
  }, [tab, serviceTypeFilter, dateRangeFilter, sortOrder, loadFiltered]);

  const activeDisplay = useMemo(() => active.map(toDisplayBooking), [active]);
  const pastDisplay = useMemo(() => past.map(toDisplayBooking), [past]);
  const filteredDisplay = useMemo(() => filtered.map(toDisplayBooking), [filtered]);

  const list = tab === "active" ? activeDisplay : pastDisplay;
  const baseList =
    tab === "active" ? filteredDisplay.filter((b) => ACTIVE_STATUSES.includes(b.status)) : pastDisplay;

  const filteredList = baseList
    .filter((b) => matchesSearch(b, searchText))
    .filter((b) => !statusFilter || b.status === statusFilter);

  const selected = filteredList.find((b) => b.id === selectedId) || filteredList[0];

  function switchTab(next) {
    setTab(next);
    setSearchText("");
    setServiceTypeFilter("");
    setStatusFilter("");
    setDateRangeFilter("");
    setSortOrder("newest");
    setSelectedId(null);
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

      {(listError || (tab === "active" && filterError)) && (
        <div className="mx-8 mt-4 text-sm text-red-600 bg-red-50 border border-red-100 rounded-lg px-3 py-2.5" role="alert">
          {listError || filterError}
        </div>
      )}

      <div className="flex gap-6 p-8">
        {(tab === "active" ? filterLoading && filteredList.length === 0 : listLoading && list.length === 0) ? (
          <div className="flex-1 text-sm text-gray-400 py-12 text-center">Loading bookings…</div>
        ) : (
          <>
            <BookingTable
              tab={tab}
              switchTab={switchTab}
              searchText={searchText}
              setSearchText={setSearchText}
              serviceTypeFilter={serviceTypeFilter}
              setServiceTypeFilter={setServiceTypeFilter}
              statusFilter={statusFilter}
              setStatusFilter={setStatusFilter}
              dateRangeFilter={dateRangeFilter}
              setDateRangeFilter={setDateRangeFilter}
              sortOrder={sortOrder}
              setSortOrder={setSortOrder}
              list={list}
              filteredList={filteredList}
              selectedId={selected?.id}
              setSelectedId={setSelectedId}
            />

            {selected && (
              <BookingDetailPanel
                selected={selected}
                tab={tab}
                onConfirmBooking={() => setConfirmingBooking(selected)}
                onAssignTechnician={() => setAssigningBooking(selected)}
                onCancelBooking={() => setCancellingBooking(selected)}
              />
            )}
          </>
        )}
      </div>

      <ConfirmBookingModal
        key={confirmingBooking?.id}
        booking={confirmingBooking}
        confirming={confirming}
        confirmError={confirmError}
        onCancel={() => setConfirmingBooking(null)}
        onConfirm={async (serviceFee, durationMinutes) => {
          const result = await confirm(confirmingBooking.id, {
            estimatedServiceFee: serviceFee,
            estimatedDurationMinutes: durationMinutes,
          });
          if (result) setConfirmingBooking(null);
        }}
      />

      <AssignTechnicianModal
        booking={assigningBooking}
        onCancel={() => setAssigningBooking(null)}
        onAssign={() => {
          // MOCK ONLY — real version calls PATCH /api/v1/bookings/{id}/assign,
          // which creates TechnicianAssignment records (response starts as
          // Pending until the Technician accepts/rejects — 3.3.M4).
          setAssigningBooking(null);
        }}
      />

      <CancelBookingModal
        booking={cancellingBooking}
        onBack={() => setCancellingBooking(null)}
        onConfirm={() => {
          // MOCK ONLY — real version calls PATCH /api/v1/bookings/{id}/cancel
          // (creates a BookingCancellation record, same pattern as /assign).
          setCancellingBooking(null);
        }}
      />
    </div>
  );
}
