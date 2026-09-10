import { useEffect, useMemo, useState } from "react";
import { useSelector } from "react-redux";
import { useTechnicianSchedule } from "../../../common/hooks/useTechnicianSchedule";
import { useTechnicianBookings } from "../../../common/hooks/useTechnicianBookings";
import {
  toDisplayTechnicianBooking,
  technicianBookingTab,
  matchesSearch,
  ASSIGNMENT_STATUS_STYLES,
  BOOKING_STATUS_STYLES,
} from "../technicianBookingHelpers";
import CompleteBookingModal from "../components/CompleteBookingModal";
import AbortBookingModal from "../components/AbortBookingModal";

const TABS = [
  { key: "assigned", label: "Assigned" },
  { key: "working", label: "Working" },
  { key: "history", label: "History" },
];

function Pill({ text, className }) {
  return (
    <span className={`text-[11px] font-semibold px-2.5 py-1 rounded-full whitespace-nowrap ${className}`}>
      {text}
    </span>
  );
}

function BookingCard({ booking, busy, onAccept, onReject, onStart, onOpenComplete, onOpenAbort }) {
  return (
    <div className="bg-white rounded-xl shadow-sm p-5 flex flex-col gap-3">
      <div className="flex items-start justify-between gap-3">
        <div>
          <div className="text-[11px] font-mono text-gray-400">{booking.bookingId}</div>
          <div className="text-sm font-bold text-gray-900 mt-0.5">{booking.service || "—"}</div>
        </div>
        <div className="flex gap-1.5 flex-wrap justify-end">
          <Pill
            text={booking.bookingStatus}
            className={BOOKING_STATUS_STYLES[booking.bookingStatus] || "bg-gray-100 text-gray-700"}
          />
          <Pill
            text={booking.assignmentStatus}
            className={ASSIGNMENT_STATUS_STYLES[booking.assignmentStatus] || "bg-gray-100 text-gray-700"}
          />
        </div>
      </div>

      {booking.issueDescription && <p className="text-xs text-gray-500">{booking.issueDescription}</p>}

      <div className="flex flex-wrap gap-x-5 gap-y-1 text-xs text-gray-500">
        {booking.address && <span>📍 {booking.address}</span>}
        {booking.preferredDate && <span>📅 {booking.preferredDate}</span>}
        {booking.availableWindow && <span>🕐 {booking.availableWindow}</span>}
        {booking.totalCost && <span>💰 {booking.totalCost}</span>}
      </div>

      <div className="flex justify-end gap-2 pt-1">
        {booking.assignmentStatus === "PENDING" && (
          <>
            <button
              disabled={busy}
              onClick={() => onReject(booking)}
              className="text-xs font-bold text-red-600 border border-red-200 hover:bg-red-50 disabled:opacity-40 rounded-lg px-3.5 py-2"
            >
              Reject
            </button>
            <button
              disabled={busy}
              onClick={() => onAccept(booking)}
              className="text-xs font-bold text-white bg-green-600 hover:bg-green-700 disabled:opacity-40 rounded-lg px-3.5 py-2"
            >
              Accept
            </button>
          </>
        )}

        {booking.assignmentStatus === "ACCEPTED" && booking.bookingStatus === "SCHEDULED" && (
          <button
            disabled={busy}
            onClick={() => onStart(booking)}
            className="text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 rounded-lg px-3.5 py-2"
          >
            Start Work
          </button>
        )}

        {booking.assignmentStatus === "ACCEPTED" && booking.bookingStatus === "WORKING" && (
          <>
            <button
              disabled={busy}
              onClick={() => onOpenAbort(booking)}
              className="text-xs font-bold text-red-600 border border-red-200 hover:bg-red-50 disabled:opacity-40 rounded-lg px-3.5 py-2"
            >
              Abort
            </button>
            <button
              disabled={busy}
              onClick={() => onOpenComplete(booking)}
              className="text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 rounded-lg px-3.5 py-2"
            >
              Complete
            </button>
          </>
        )}
      </div>
    </div>
  );
}

export default function TechnicianBookingPage() {
  const user = useSelector((state) => state.auth.user);
  const isAdmin = user?.role === "ADMIN";

  const { profile, profileLoading, profileError, loadMyProfile } = useTechnicianSchedule();
  const {
    bookings,
    bookingsLoading,
    bookingsError,
    bookingActionLoading,
    bookingActionError,
    loadBookings,
    accept,
    reject,
    start,
    complete,
    abort,
  } = useTechnicianBookings();

  const [activeTab, setActiveTab] = useState("assigned");
  const [search, setSearch] = useState("");
  const [completingBooking, setCompletingBooking] = useState(null);
  const [abortingBooking, setAbortingBooking] = useState(null);

  useEffect(() => {
    if (isAdmin || !user?.staffId) return;
    (async () => {
      const loadedProfile = await loadMyProfile(user.staffId);
      if (loadedProfile) loadBookings(loadedProfile.id);
    })();
  }, [isAdmin, user?.staffId, loadMyProfile, loadBookings]);

  const displayBookings = useMemo(() => (bookings || []).map(toDisplayTechnicianBooking), [bookings]);

  const grouped = useMemo(() => {
    const groups = { assigned: [], working: [], history: [] };
    displayBookings
      .filter((b) => matchesSearch(b, search))
      .forEach((b) => groups[technicianBookingTab(b)].push(b));
    return groups;
  }, [displayBookings, search]);

  if (isAdmin) {
    return (
      <div className="bg-gray-50 min-h-screen">
        <div className="bg-white border-b border-gray-200 px-8 py-4">
          <h1 className="text-lg font-extrabold text-gray-900">My Bookings</h1>
        </div>
        <p className="text-sm text-gray-400 px-8 py-6">
          Admins manage technician assignments from the Bookings page — this per-technician view
          is only for technician accounts.
        </p>
      </div>
    );
  }

  if (profileLoading) return <p className="text-sm text-gray-400 px-8 py-6">Loading your bookings…</p>;
  if (profileError) return <p className="text-sm text-red-500 px-8 py-6">{profileError}</p>;

  const technicianId = profile?.id;

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4 flex items-center justify-between gap-4">
        <div>
          <h1 className="text-lg font-extrabold text-gray-900">My Bookings</h1>
          <p className="text-xs text-gray-400 mt-0.5">{profile?.fullName}</p>
        </div>
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by booking ID, service, or address…"
          className="w-72 text-sm border border-gray-200 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      <div className="flex gap-2 px-8 pt-4">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`text-xs font-bold px-4 py-2 rounded-lg transition-colors ${
              activeTab === tab.key
                ? "bg-indigo-600 text-white"
                : "bg-white text-gray-500 border border-gray-200 hover:bg-gray-100"
            }`}
          >
            {tab.label} ({grouped[tab.key].length})
          </button>
        ))}
      </div>

      {bookingActionError && (
        <p className="mx-8 mt-4 text-xs text-red-600 bg-red-50 border border-red-100 rounded-lg px-3 py-2.5">
          {bookingActionError}
        </p>
      )}

      <div className="p-8 pt-4 flex flex-col gap-3 max-w-3xl">
        {bookingsLoading && <p className="text-sm text-gray-400">Loading bookings…</p>}
        {!bookingsLoading && bookingsError && <p className="text-sm text-red-500">{bookingsError}</p>}
        {!bookingsLoading && !bookingsError && grouped[activeTab].length === 0 && (
          <p className="text-sm text-gray-400">No bookings in this tab.</p>
        )}
        {!bookingsLoading &&
          grouped[activeTab].map((booking) => (
            <BookingCard
              key={booking.assignmentId}
              booking={booking}
              busy={bookingActionLoading}
              onAccept={(b) => accept(technicianId, b.bookingId)}
              onReject={(b) => reject(technicianId, b.bookingId)}
              onStart={(b) => start(technicianId, b.bookingId)}
              onOpenComplete={setCompletingBooking}
              onOpenAbort={setAbortingBooking}
            />
          ))}
      </div>

      {completingBooking && (
        <CompleteBookingModal
          booking={completingBooking}
          submitting={bookingActionLoading}
          submitError={bookingActionError}
          onBack={() => setCompletingBooking(null)}
          onConfirm={async (payload) => {
            const ok = await complete(technicianId, completingBooking.bookingId, payload);
            if (ok) setCompletingBooking(null);
          }}
        />
      )}

      {abortingBooking && (
        <AbortBookingModal
          booking={abortingBooking}
          submitting={bookingActionLoading}
          submitError={bookingActionError}
          onBack={() => setAbortingBooking(null)}
          onConfirm={async (reason) => {
            const ok = await abort(technicianId, abortingBooking.bookingId, reason);
            if (ok) setAbortingBooking(null);
          }}
        />
      )}
    </div>
  );
}
