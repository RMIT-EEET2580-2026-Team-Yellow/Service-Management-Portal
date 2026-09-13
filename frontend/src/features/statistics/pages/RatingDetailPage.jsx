import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useStatistics } from "../../../common/hooks/useStatistics";

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
  const { bookingReview, bookingReviewLoading, bookingReviewError, loadBookingReview, clearReview } =
    useStatistics();

  useEffect(() => {
    loadBookingReview(bookingId);
    return () => clearReview();
  }, [bookingId, loadBookingReview, clearReview]);

  const subRatings = bookingReview
    ? [
        { label: "Integrity", value: bookingReview.integrity },
        { label: "Quality", value: bookingReview.quality },
        { label: "Friendliness", value: bookingReview.friendliness },
      ]
    : [];

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-slate-950">
      <div className="border-b border-gray-200 bg-white px-4 py-4 dark:border-slate-800 dark:bg-slate-900 sm:px-6 lg:px-8">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-lg font-extrabold text-gray-900 dark:text-white"
        >
          ← Booking #{bookingId.slice(0, 8)}…
        </button>
        <p className="mt-0.5 text-xs text-gray-400 dark:text-slate-500">Statistics / Rating Detail</p>
      </div>

      <div className="flex justify-center p-4 sm:p-6 lg:p-8">
        <div className="w-full max-w-[640px]">
          {bookingReviewLoading && (
            <div className="rounded-xl bg-white p-6 text-center text-sm text-gray-400 shadow-sm dark:bg-slate-900 dark:text-slate-500">
              Loading review…
            </div>
          )}

          {bookingReviewError && (
            <div className="rounded-xl bg-white p-6 text-center text-sm text-red-500 shadow-sm dark:bg-slate-900">
              {bookingReviewError}
            </div>
          )}

          {!bookingReviewLoading && !bookingReviewError && bookingReview && (
            <div className="h-fit overflow-hidden rounded-xl bg-white shadow-sm dark:bg-slate-900">
              <div className="border-b border-gray-100 px-6 py-5 text-center dark:border-slate-800">
                <div className="mb-2 text-[10px] font-bold uppercase text-gray-400 dark:text-slate-500">
                  Overall Rating
                </div>
                <div className="text-4xl font-extrabold text-gray-900 dark:text-white">
                  {bookingReview.overallStars}
                </div>
                <div className="mt-1">
                  <Stars value={bookingReview.overallStars} />
                </div>
              </div>

              <div className="border-b border-gray-100 px-6 py-5 dark:border-slate-800">
                <div className="mb-3 text-[10px] font-bold uppercase text-gray-400 dark:text-slate-500">
                  Sub-category Ratings
                </div>
                {subRatings.map((s) => (
                  <div key={s.label} className="mb-2.5 flex items-center gap-3 text-xs">
                    <span className="w-24 shrink-0 text-gray-500 dark:text-slate-400">{s.label}</span>
                    <div className="h-2 flex-1 overflow-hidden rounded-full bg-gray-100 dark:bg-slate-800">
                      <div
                        className="h-full rounded-full bg-indigo-600"
                        style={{ width: `${(s.value / 5) * 100}%` }}
                      />
                    </div>
                    <span className="w-8 text-right font-bold text-gray-700 dark:text-slate-300">
                      {s.value}
                    </span>
                  </div>
                ))}
              </div>

              <div className="border-b border-gray-100 px-6 py-5 dark:border-slate-800">
                <div className="mb-2 text-[10px] font-bold uppercase text-gray-400 dark:text-slate-500">
                  Customer Review
                </div>
                <p className="text-sm italic leading-relaxed text-gray-700 dark:text-slate-300">
                  {bookingReview.reviewText ? `"${bookingReview.reviewText}"` : "No written review."}
                </p>
              </div>

              <div className="px-6 py-5">
                <div className="mb-3 text-[10px] font-bold uppercase text-gray-400 dark:text-slate-500">
                  Evidence Images
                </div>
                {(bookingReview.evidenceImageUrls ?? []).length === 0 ? (
                  <p className="text-xs text-gray-400 dark:text-slate-500">No evidence images uploaded.</p>
                ) : (
                  <div className="flex flex-wrap gap-2.5">
                    {bookingReview.evidenceImageUrls.map((url) => (
                      <a key={url} href={url} target="_blank" rel="noreferrer">
                        <img
                          src={url}
                          alt="Rating evidence"
                          className="h-[90px] w-[90px] rounded-lg object-cover"
                        />
                      </a>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}