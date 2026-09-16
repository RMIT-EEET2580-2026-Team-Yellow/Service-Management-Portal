import { useNavigate, useParams } from "react-router-dom";

/**
 * MOCK UI ONLY — no backend wired up yet (3.4.S2, M1, M2).
 * Real version calls GET /api/statistics/ratings, which internally proxies
 * to SBP's rating aggregate API (SRS 3.4.S1/S5) — must not query a local
 * rating table. Technicians must not access this page (3.4.S4/M4).
 */

const MOCK_RATING = {
  "B-0998": {
    service: "Aircon Repair Service",
    completedDate: "05 Aug 2026",
    technician: "Nguyễn Văn A",
    overall: 4.6,
    subRatings: [
      { label: "Integrity", value: 4.8 },
      { label: "Quality", value: 4.5 },
      { label: "Friendliness", value: 4.6 },
    ],
    review: "Great service! Very professional and friendly. Will book again.",
    evidenceImages: 2,
  },
};

function Stars({ value }) {
  const full = Math.round(value);
  return (
    <div className="flex justify-center gap-1 text-xl text-amber-400">
      {Array.from({ length: 5 }).map((_, i) => (
        <span key={i}>{i < full ? "★" : "☆"}</span>
      ))}
    </div>
  );
}

export default function RatingDetailPage() {
  const navigate = useNavigate();
  const { bookingId } = useParams();
  const rating = MOCK_RATING[bookingId] || MOCK_RATING["B-0998"];

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4">
        <button
          onClick={() => navigate(-1)}
          className="text-lg font-extrabold text-gray-900 flex items-center gap-2"
        >
          ← Booking #{bookingId}
        </button>
        <p className="text-xs text-gray-400 mt-0.5">Statistics / Rating Detail</p>
      </div>

      <div className="flex justify-center p-8">
        <div className="w-[640px] bg-white rounded-xl shadow-sm overflow-hidden h-fit">
          <div className="px-6 py-5 border-b border-gray-100 text-center">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-2">
              Overall Rating
            </div>
            <div className="text-4xl font-extrabold text-gray-900">{rating.overall}</div>
            <div className="mt-1">
              <Stars value={rating.overall} />
            </div>
            <div className="text-xs text-gray-400 mt-2">
              {rating.service} · Completed {rating.completedDate} · Technician: {rating.technician}
            </div>
          </div>

          <div className="px-6 py-5 border-b border-gray-100">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-3">
              Sub-category Ratings
            </div>
            {rating.subRatings.map((s) => (
              <div key={s.label} className="flex items-center gap-3 text-xs mb-2.5">
                <span className="w-24 shrink-0 text-gray-500">{s.label}</span>
                <div className="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-indigo-600 rounded-full"
                    style={{ width: `${(s.value / 5) * 100}%` }}
                  />
                </div>
                <span className="w-8 text-right font-bold">{s.value}</span>
              </div>
            ))}
          </div>

          <div className="px-6 py-5 border-b border-gray-100">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-2">
              Customer Review
            </div>
            <p className="text-sm italic text-gray-700 leading-relaxed">"{rating.review}"</p>
          </div>

          <div className="px-6 py-5">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-3">
              Evidence Images
            </div>
            <div className="flex gap-2.5">
              {Array.from({ length: rating.evidenceImages }).map((_, i) => (
                <div
                  key={i}
                  className="w-[90px] h-[90px] bg-gray-100 rounded-lg flex items-center justify-center text-gray-300 text-xl"
                >
                  🖼️
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
