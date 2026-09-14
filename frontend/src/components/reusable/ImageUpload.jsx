import { useRef, useState } from "react";
import { uploadImage } from "../../common/api/fileApi";

const ALLOWED_TYPES = ["image/jpeg", "image/png", "image/webp"];
const MAX_SIZE_BYTES = 5 * 1024 * 1024;

/**
 * Reusable Components Tier (A.1.1) — upload-and-preview widget shared by every image-evidence
 * feature (CancelBookingModal's evidence photos, CompleteBookingModal's signed bill). Client-side
 * validation here mirrors FileStorageService's rules so the user gets instant feedback (A.1.M4);
 * the backend re-validates independently since this check is bypassable.
 *
 * `urls` is always an array — pass multiple={false} to cap it at one entry.
 */
export default function ImageUpload({ label, urls, onChange, multiple = true, maxFiles = 5 }) {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");
  const inputRef = useRef(null);

  const atLimit = multiple && urls.length >= maxFiles;

  function validate(file) {
    if (!ALLOWED_TYPES.includes(file.type)) {
      return "Only JPEG, PNG, or WEBP images are allowed.";
    }
    if (file.size > MAX_SIZE_BYTES) {
      return "File exceeds the 5MB limit.";
    }
    return null;
  }

  async function handleFileSelect(e) {
    const file = e.target.files?.[0];
    e.target.value = "";
    if (!file) return;

    const validationError = validate(file);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError("");
    setUploading(true);
    try {
      const { url } = await uploadImage(file);
      onChange(multiple ? [...urls, url] : [url]);
    } catch (err) {
      setError(err.message || "Upload failed.");
    } finally {
      setUploading(false);
    }
  }

  function handleRemove(url) {
    onChange(urls.filter((u) => u !== url));
  }

  return (
    <div>
      {label && (
        <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">{label}</label>
      )}

      {error && <p className="text-[11px] text-red-600 mb-1.5">{error}</p>}

      {urls.length > 0 && (
        <div className="flex flex-wrap gap-2 mb-2">
          {urls.map((url) => (
            <div key={url} className="relative w-16 h-16">
              <img
                src={url}
                alt="Evidence"
                className="w-16 h-16 object-cover rounded-lg border border-gray-200"
              />
              <button
                type="button"
                onClick={() => handleRemove(url)}
                className="absolute -top-1.5 -right-1.5 w-4.5 h-4.5 flex items-center justify-center bg-red-500 text-white text-[10px] rounded-full leading-none"
                aria-label="Remove image"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      )}

      {!atLimit && (
        <label
          className={`block border-2 border-dashed border-indigo-200 bg-indigo-50 text-indigo-600 text-xs font-semibold rounded-lg px-4 py-5 text-center cursor-pointer ${
            uploading ? "opacity-60 cursor-wait" : ""
          }`}
        >
          {uploading ? "Uploading…" : "📷 Upload evidence image"}
          <input
            ref={inputRef}
            type="file"
            accept={ALLOWED_TYPES.join(",")}
            onChange={handleFileSelect}
            disabled={uploading}
            className="hidden"
          />
        </label>
      )}
    </div>
  );
}
