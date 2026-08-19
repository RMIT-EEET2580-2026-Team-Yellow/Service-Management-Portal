import { validateEmail, validatePhone, passwordChecks } from "../staffValidation";

/**
 * MOCK UI — Step 1 of Staff creation (SRS 3.1.S1). Split out of
 * StaffCreatePage.jsx to keep that file short; all state still lives in the
 * parent, this component only renders the form and calls the setters passed
 * in as props.
 */

function CheckItem({ ok, label }) {
  return (
    <div className={`text-xs flex items-center gap-1.5 ${ok ? "text-green-600" : "text-gray-400"}`}>
      <span>{ok ? "✓" : "○"}</span>
      <span>{label}</span>
    </div>
  );
}

export default function RegisterStaffForm({
  role,
  setRole,
  fullName,
  setFullName,
  email,
  setEmail,
  phone,
  setPhone,
  password,
  setPassword,
  confirmPassword,
  setConfirmPassword,
  streetNumber,
  setStreetNumber,
  streetName,
  setStreetName,
  ward,
  setWard,
  province,
  setProvince,
  provinces,
  onCancel,
  onContinue,
}) {
  const emailValid = email === "" || validateEmail(email);
  const phoneValid = phone === "" || validatePhone(phone);
  const checks = passwordChecks(password);
  const passwordValid = Object.values(checks).every(Boolean);
  const confirmValid = confirmPassword === "" || confirmPassword === password;

  const formValid =
    fullName &&
    email &&
    validateEmail(email) &&
    phone &&
    validatePhone(phone) &&
    passwordValid &&
    confirmPassword === password &&
    streetNumber &&
    streetName &&
    ward &&
    province;

  return (
    <div className="w-[420px] bg-white rounded-xl shadow-sm p-6">
      <div className="text-[10px] font-bold uppercase text-gray-400 mb-4">
        Register New Staff
      </div>

      {/* Role toggle */}
      <div className="flex gap-3 mb-5">
        <button
          onClick={() => {
            setRole("Receptionist");
            setProvince("");
          }}
          className={`flex-1 text-sm font-bold py-2.5 rounded-lg border ${
            role === "Receptionist"
              ? "border-indigo-600 bg-indigo-50 text-indigo-700"
              : "border-gray-200 text-gray-500"
          }`}
        >
          📋 Receptionist
        </button>
        <button
          onClick={() => {
            setRole("Technician");
            setProvince("");
          }}
          className={`flex-1 text-sm font-bold py-2.5 rounded-lg border ${
            role === "Technician"
              ? "border-indigo-600 bg-indigo-50 text-indigo-700"
              : "border-gray-200 text-gray-500"
          }`}
        >
          👤 Technician
        </button>
      </div>

      {/* Full Name */}
      <div className="mb-5">
        <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
          Full Name
        </label>
        <input
          value={fullName}
          onChange={(e) => setFullName(e.target.value)}
          placeholder="Nguyễn Văn A"
          className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      {/* Email + Phone */}
      <div className="grid grid-cols-2 gap-3 mb-1">
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Email
          </label>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="reception@fixinh.vn"
            className={`w-full text-sm border rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 ${
              email && !emailValid
                ? "border-red-400 bg-red-50 focus:ring-red-300"
                : "border-gray-200 focus:ring-indigo-500"
            }`}
          />
        </div>
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Phone Number
          </label>
          <input
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="0912345678"
            className={`w-full text-sm border rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 ${
              phone && !phoneValid
                ? "border-red-400 bg-red-50 focus:ring-red-300"
                : phone
                ? "border-green-400 focus:ring-green-300"
                : "border-gray-200 focus:ring-indigo-500"
            }`}
          />
        </div>
      </div>
      <div className="grid grid-cols-2 gap-3 mb-5">
        <p className={`text-xs ${email && !emailValid ? "text-red-500" : "text-transparent"}`}>
          ⚠ Invalid email — must contain &quot;@&quot; and a &quot;.&quot; after it. E.g.
          reception@fixinh.vn
        </p>
        <p className={`text-xs ${phone ? (phoneValid ? "text-green-600" : "text-red-500") : "text-transparent"}`}>
          {phoneValid ? "✓" : "⚠"} Valid — 7-11 digits, starts with 0
        </p>
      </div>

      {/* Password + Confirm */}
      <div className="grid grid-cols-2 gap-3 mb-2">
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Password
          </label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Confirm Password
          </label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className={`w-full text-sm border rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 ${
              confirmPassword && !confirmValid
                ? "border-red-400 bg-red-50 focus:ring-red-300"
                : "border-gray-200 focus:ring-indigo-500"
            }`}
          />
        </div>
      </div>
      <div className="mb-5 space-y-1">
        <CheckItem ok={checks.length} label="At least 8 characters" />
        <CheckItem ok={checks.number} label="At least 1 number" />
        <CheckItem ok={checks.special} label="At least 1 special character ($#@!)" />
        <CheckItem ok={checks.capital} label="At least 1 capital letter" />
        {confirmPassword && !confirmValid && (
          <div className="text-xs text-red-500">⚠ Passwords do not match</div>
        )}
      </div>

      <div className="border-t border-gray-100 -mx-6 mb-5" />

      {/* Branch Address */}
      <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">
        Branch Address
      </div>
      <div className="grid grid-cols-2 gap-3 mb-3">
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Street Number
          </label>
          <input
            value={streetNumber}
            onChange={(e) => setStreetNumber(e.target.value)}
            className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Street Name
          </label>
          <input
            value={streetName}
            onChange={(e) => setStreetName(e.target.value)}
            className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
      </div>
      <div className="grid grid-cols-2 gap-3 mb-6">
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Ward
          </label>
          <input
            value={ward}
            onChange={(e) => setWard(e.target.value)}
            className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label className="text-[10px] font-bold uppercase text-gray-400 block mb-1">
            Province
          </label>
          <select
            value={province}
            onChange={(e) => setProvince(e.target.value)}
            className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Select province…</option>
            {provinces.map((p) => (
              <option key={p.id} value={p.name}>
                {p.name} ({p.region})
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="flex justify-end gap-2.5">
        <button
          onClick={onCancel}
          className="border border-gray-200 text-gray-600 text-sm font-bold px-4 py-2.5 rounded-lg hover:bg-gray-50"
        >
          Cancel
        </button>
        <button
          onClick={onContinue}
          disabled={!formValid}
          className={`text-sm font-bold px-4 py-2.5 rounded-lg text-white ${
            formValid ? "bg-indigo-600 hover:bg-indigo-700" : "bg-indigo-300 cursor-not-allowed"
          }`}
        >
          {role === "Technician" ? "Continue → Set Availability" : "Create Account"}
        </button>
      </div>
    </div>
  );
}
