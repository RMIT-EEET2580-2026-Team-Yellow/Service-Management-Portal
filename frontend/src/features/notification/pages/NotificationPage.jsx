import { useEffect } from "react";
import { Link } from "react-router-dom";
import { useNotifications } from "../../../common/hooks/useNotifications";

function formatTimestamp(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleString("en-GB", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function NotificationPage() {
  const { items, unreadCount, loading, error, load, read, clearAll } = useNotifications();

  useEffect(() => {
    load();
  }, [load]);

  function handleClearAll() {
    if (window.confirm("Clear all notifications? This cannot be undone.")) {
      clearAll();
    }
  }

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4 flex items-center justify-between">
        <div>
          <h1 className="text-lg font-extrabold text-gray-900">Notifications</h1>
          <p className="text-xs text-gray-400 mt-0.5">
            {unreadCount > 0 ? `${unreadCount} unread` : "You're all caught up"}
          </p>
        </div>
        {items.length > 0 && (
          <button
            onClick={handleClearAll}
            className="shrink-0 text-xs font-bold text-gray-500 hover:text-red-600 whitespace-nowrap"
          >
            Clear all
          </button>
        )}
      </div>

      <div className="p-8 max-w-2xl">
        {error && (
          <div className="mb-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
        )}

        {loading && items.length === 0 && (
          <div className="text-sm text-gray-400 px-2 py-6">Loading notifications…</div>
        )}

        {!loading && items.length === 0 && !error && (
          <div className="bg-white rounded-xl shadow-sm px-6 py-10 text-center text-sm text-gray-400">
            No notifications yet. You'll be notified here when one of your bookings is completed,
            cancelled, terminated, or a technician aborts the job.
          </div>
        )}

        <div className="flex flex-col gap-2.5">
          {items.map((n) => (
            <div
              key={n.id}
              className={`bg-white rounded-xl shadow-sm px-5 py-4 flex items-start justify-between gap-4 ${
                n.read ? "" : "border-l-4 border-indigo-500"
              }`}
            >
              <div className="min-w-0">
                {/* Links to the Bookings list rather than a not-yet-built detail page —
                    BookingDetailPage.jsx is still a stub with no route param wired up. */}
                <Link
                  to="/bookings"
                  className="text-sm font-semibold text-gray-900 hover:text-indigo-600"
                >
                  {n.message}
                </Link>
                <div className="text-[11px] text-gray-400 mt-1">{formatTimestamp(n.createdAt)}</div>
              </div>

              {!n.read && (
                <button
                  onClick={() => read(n.id)}
                  className="shrink-0 text-xs font-bold text-indigo-600 hover:text-indigo-800 whitespace-nowrap"
                >
                  Mark as read
                </button>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
