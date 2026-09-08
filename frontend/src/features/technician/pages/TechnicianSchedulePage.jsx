import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useTechnicianSchedule } from "../../../common/hooks/useTechnicianSchedule";

/**
 * A Technician submits a new working schedule here; it only replaces their live
 * TechnicianAvailability once an Admin approves it (see TechnicianServiceImpl
 * .approveScheduleChangeRequest). Admin sees the same route rendered as a review queue.
 */

const DAY_ORDER = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"];
const DAY_LABELS = { MON: "Mon", TUE: "Tue", WED: "Wed", THU: "Thu", FRI: "Fri", SAT: "Sat", SUN: "Sun" };

const STATUS_STYLES = {
  PENDING: "bg-amber-100 text-amber-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};

function toHHMM(time) {
  return time ? time.slice(0, 5) : "";
}

function formatDays(days) {
  return (days || []).map((d) => DAY_LABELS[d] || d).join(", ") || "—";
}

function formatDateTime(value) {
  return value ? new Date(value).toLocaleString() : "—";
}

const REQUIRED_WORK_DAYS = 5;

function TechnicianScheduleForm({ profile, submitLoading, submitError, hasPending, onSubmit }) {
  const [worksOn, setWorksOn] = useState(() => {
    const current = new Set(profile?.workingDays || []);
    return Object.fromEntries(DAY_ORDER.map((d) => [d, current.has(d)]));
  });

  const workingDays = DAY_ORDER.filter((d) => worksOn[d]);
  const isValidCount = workingDays.length === REQUIRED_WORK_DAYS;

  if (hasPending) {
    return (
      <div className="bg-amber-50 border border-amber-200 text-amber-800 rounded-xl p-4 text-sm">
        You already have a schedule change request pending Admin approval. You can submit a
        new one once it has been approved or rejected.
      </div>
    );
  }

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        if (!isValidCount) return;
        onSubmit({ workingDays });
      }}
      className="bg-white rounded-xl shadow-sm p-5 flex flex-col gap-4"
    >
      <div>
        <label className="block text-xs font-bold text-gray-500 uppercase mb-2">
          Pick exactly {REQUIRED_WORK_DAYS} working days (any combination) — the other 2 are days off
        </label>
        <div className="flex flex-col divide-y divide-gray-100 rounded-lg border border-gray-200 overflow-hidden">
          {DAY_ORDER.map((d) => (
            <label
              key={d}
              className="flex items-center justify-between px-3 py-2.5 text-sm cursor-pointer hover:bg-gray-50"
            >
              <span className="font-medium text-gray-700">{DAY_LABELS[d]}</span>
              <span className="flex items-center gap-2">
                <span className={worksOn[d] ? "text-green-700 font-semibold" : "text-gray-400"}>
                  {worksOn[d] ? "Working" : "Off"}
                </span>
                <input
                  type="checkbox"
                  checked={!!worksOn[d]}
                  onChange={(e) => setWorksOn((prev) => ({ ...prev, [d]: e.target.checked }))}
                  className="h-4 w-4 accent-indigo-600"
                />
              </span>
            </label>
          ))}
        </div>
        <p className={`text-xs mt-2 ${isValidCount ? "text-gray-400" : "text-red-500 font-medium"}`}>
          {workingDays.length}/{REQUIRED_WORK_DAYS} working days selected
          {!isValidCount && ` — must select exactly ${REQUIRED_WORK_DAYS}`}
        </p>
      </div>

      <p className="text-xs text-gray-400">Working hours are fixed at 08:00 – 20:00 on every working day.</p>

      {submitError && <p className="text-sm text-red-500">{submitError}</p>}

      <button
        type="submit"
        disabled={submitLoading || !isValidCount}
        className="self-start text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 rounded-lg px-4 py-2.5"
      >
        {submitLoading ? "Submitting…" : "Submit for approval"}
      </button>
    </form>
  );
}

function TechnicianView({ staffId }) {
  const {
    profile,
    profileLoading,
    profileError,
    myRequests,
    myRequestsLoading,
    myRequestsError,
    submitLoading,
    submitError,
    loadMyProfile,
    loadMyRequests,
    submitRequest,
  } = useTechnicianSchedule();

  useEffect(() => {
    (async () => {
      const loadedProfile = await loadMyProfile(staffId);
      if (loadedProfile) loadMyRequests(loadedProfile.id);
    })();
  }, [staffId, loadMyProfile, loadMyRequests]);

  const hasPending = myRequests.some((r) => r.status === "PENDING");

  const handleSubmit = async (schedule) => {
    if (!profile) return;
    const ok = await submitRequest(profile.id, schedule);
    if (ok) loadMyRequests(profile.id);
  };

  if (profileLoading) return <p className="text-sm text-gray-400 px-8 py-6">Loading your schedule…</p>;
  if (profileError) return <p className="text-sm text-red-500 px-8 py-6">{profileError}</p>;

  return (
    <div className="p-8 flex flex-col gap-6 max-w-2xl">
      <div className="bg-white rounded-xl shadow-sm p-5">
        <h2 className="text-sm font-bold text-gray-900 mb-2">Current schedule</h2>
        <p className="text-sm text-gray-600">{formatDays(profile?.workingDays)}</p>
        <p className="text-sm text-gray-600">
          {toHHMM(profile?.dailyStart)} – {toHHMM(profile?.dailyEnd)}
        </p>
      </div>

      <div>
        <h2 className="text-sm font-bold text-gray-900 mb-2">Request a new schedule</h2>
        <TechnicianScheduleForm
          profile={profile}
          submitLoading={submitLoading}
          submitError={submitError}
          hasPending={hasPending}
          onSubmit={handleSubmit}
        />
      </div>

      <div>
        <h2 className="text-sm font-bold text-gray-900 mb-2">Request history</h2>
        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 text-[11px] uppercase tracking-wide text-gray-400">
                <th className="text-left font-bold px-4 py-3">Requested schedule</th>
                <th className="text-left font-bold px-4 py-3">Submitted</th>
                <th className="text-left font-bold px-4 py-3">Status</th>
                <th className="text-left font-bold px-4 py-3">Note</th>
              </tr>
            </thead>
            <tbody>
              {myRequestsLoading && (
                <tr>
                  <td colSpan={4} className="px-4 py-6 text-center text-gray-400">Loading…</td>
                </tr>
              )}
              {!myRequestsLoading && myRequestsError && (
                <tr>
                  <td colSpan={4} className="px-4 py-6 text-center text-red-500">{myRequestsError}</td>
                </tr>
              )}
              {!myRequestsLoading && !myRequestsError && myRequests.length === 0 && (
                <tr>
                  <td colSpan={4} className="px-4 py-6 text-center text-gray-400">No requests yet.</td>
                </tr>
              )}
              {!myRequestsLoading &&
                myRequests.map((r) => (
                  <tr key={r.id} className="border-t border-gray-100">
                    <td className="px-4 py-3.5">
                      {formatDays(r.requestedWorkingDays)}, {toHHMM(r.requestedDailyStart)}–{toHHMM(r.requestedDailyEnd)}
                    </td>
                    <td className="px-4 py-3.5 text-gray-500">{formatDateTime(r.requestedAt)}</td>
                    <td className="px-4 py-3.5">
                      <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${STATUS_STYLES[r.status]}`}>
                        {r.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-gray-500">
                      {r.status === "REJECTED" ? (r.rejectReason || "—") : "—"}
                    </td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function AdminView() {
  const {
    pendingRequests,
    pendingLoading,
    pendingError,
    actionLoading,
    actionError,
    loadPendingRequests,
    approveRequest,
    rejectRequest,
  } = useTechnicianSchedule();
  const [rejectingId, setRejectingId] = useState(null);
  const [reason, setReason] = useState("");

  useEffect(() => {
    loadPendingRequests();
  }, [loadPendingRequests]);

  const handleApprove = async (id) => {
    const ok = await approveRequest(id);
    if (ok) loadPendingRequests();
  };

  const handleReject = async (id) => {
    const ok = await rejectRequest(id, reason);
    if (ok) {
      setRejectingId(null);
      setReason("");
      loadPendingRequests();
    }
  };

  return (
    <div className="p-8">
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-[11px] uppercase tracking-wide text-gray-400">
              <th className="text-left font-bold px-4 py-3">Technician</th>
              <th className="text-left font-bold px-4 py-3">Current schedule</th>
              <th className="text-left font-bold px-4 py-3">Requested schedule</th>
              <th className="text-left font-bold px-4 py-3">Submitted</th>
              <th className="text-left font-bold px-4 py-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            {pendingLoading && (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-gray-400">Loading…</td>
              </tr>
            )}
            {!pendingLoading && pendingError && (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-red-500">{pendingError}</td>
              </tr>
            )}
            {!pendingLoading && !pendingError && pendingRequests.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-gray-400">No pending schedule requests.</td>
              </tr>
            )}
            {!pendingLoading &&
              pendingRequests.map((r) => (
                <tr key={r.id} className="border-t border-gray-100 align-top">
                  <td className="px-4 py-3.5 font-bold text-gray-900">{r.technicianFullName}</td>
                  <td className="px-4 py-3.5 text-gray-500">
                    {formatDays(r.currentWorkingDays)}
                    <br />
                    {toHHMM(r.currentDailyStart)}–{toHHMM(r.currentDailyEnd)}
                  </td>
                  <td className="px-4 py-3.5 text-gray-900 font-medium">
                    {formatDays(r.requestedWorkingDays)}
                    <br />
                    {toHHMM(r.requestedDailyStart)}–{toHHMM(r.requestedDailyEnd)}
                  </td>
                  <td className="px-4 py-3.5 text-gray-500">{formatDateTime(r.requestedAt)}</td>
                  <td className="px-4 py-3.5">
                    {rejectingId === r.id ? (
                      <div className="flex flex-col gap-2 min-w-[180px]">
                        <input
                          value={reason}
                          onChange={(e) => setReason(e.target.value)}
                          placeholder="Reason (optional)"
                          className="text-xs border border-gray-200 rounded-lg px-2 py-1.5 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                        <div className="flex gap-2">
                          <button
                            disabled={actionLoading}
                            onClick={() => handleReject(r.id)}
                            className="text-xs font-bold text-white bg-red-600 hover:bg-red-700 disabled:opacity-50 rounded-lg px-3 py-1.5"
                          >
                            Confirm
                          </button>
                          <button
                            onClick={() => {
                              setRejectingId(null);
                              setReason("");
                            }}
                            className="text-xs font-bold text-gray-500 hover:bg-gray-100 rounded-lg px-3 py-1.5"
                          >
                            Cancel
                          </button>
                        </div>
                      </div>
                    ) : (
                      <div className="flex gap-2">
                        <button
                          disabled={actionLoading}
                          onClick={() => handleApprove(r.id)}
                          className="text-xs font-bold text-white bg-green-600 hover:bg-green-700 disabled:opacity-50 rounded-lg px-3 py-1.5"
                        >
                          Approve
                        </button>
                        <button
                          disabled={actionLoading}
                          onClick={() => setRejectingId(r.id)}
                          className="text-xs font-bold text-red-600 border border-red-200 hover:bg-red-50 disabled:opacity-50 rounded-lg px-3 py-1.5"
                        >
                          Reject
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>
      {actionError && <p className="text-sm text-red-500 mt-3">{actionError}</p>}
    </div>
  );
}

export default function TechnicianSchedulePage() {
  const user = useSelector((state) => state.auth.user);
  const isAdmin = user?.role === "ADMIN";

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4">
        <h1 className="text-lg font-extrabold text-gray-900">
          {isAdmin ? "Technician Schedule Requests" : "My Working Schedule"}
        </h1>
        <p className="text-xs text-gray-400 mt-0.5">
          {isAdmin
            ? "Review and approve schedule changes submitted by technicians"
            : "Request a new working schedule — it takes effect once Admin approves it"}
        </p>
      </div>

      {isAdmin ? <AdminView /> : <TechnicianView staffId={user?.staffId} />}
    </div>
  );
}
