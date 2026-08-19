import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import fetchProvinces from "../services/provinceApi";

/**
 * MOCK UI — Staff create/availability flow is still mock, but the Province
 * dropdown below is wired to the real GET /api/v1/provinces backend.
 * Matches SRS 3.1.S1: Admin registers a new Receptionist or Technician with
 * Email, Phone Number, Password, Confirm Password, Branch Address as mandatory
 * fields. Technicians additionally get a working-availability schedule
 * (3.1.M8-style) before the account is created.
 */

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
const WORKING_DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validatePhone(phone) {
  return /^0\d{6,10}$/.test(phone);
}

function passwordChecks(password) {
  return {
    length: password.length >= 8,
    number: /\d/.test(password),
    special: /[$#@!]/.test(password),
    capital: /[A-Z]/.test(password),
  };
}

function CheckItem({ ok, label }) {
  return (
    <div className={`text-xs flex items-center gap-1.5 ${ok ? "text-green-600" : "text-gray-400"}`}>
      <span>{ok ? "✓" : "○"}</span>
      <span>{label}</span>
    </div>
  );
}

export default function StaffCreatePage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);

  const [role, setRole] = useState("Receptionist");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [streetNumber, setStreetNumber] = useState("");
  const [streetName, setStreetName] = useState("");
  const [ward, setWard] = useState("");
  const [province, setProvince] = useState("");

  const [selectedDays, setSelectedDays] = useState(WORKING_DAYS);
  const [startHour, setStartHour] = useState(8);
  const [endHour, setEndHour] = useState(18);

  const [provinces, setProvinces] = useState([]);

  const fetchProvinceData = async () => {
    const data = await fetchProvinces();
    setProvinces(data);
  };

  useEffect(() => {
    fetchProvinceData();
  }, []);

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

  function handleContinue() {
    if (!formValid) return;
    if (role === "Technician") {
      setStep(2);
    } else {
      // MOCK ONLY — real version calls POST /api/v1/staff
      navigate("/staff");
    }
  }

  function toggleDay(day) {
    setSelectedDays((prev) =>
      prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day]
    );
  }

  function handleCreateAccount() {
    // MOCK ONLY — real version calls POST /api/v1/staff with availability payload
    navigate("/staff");
  }

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4">
        <h1 className="text-lg font-extrabold text-gray-900">Staff Accounts</h1>
        <p className="text-xs text-gray-400 mt-0.5">
          Admin / {step === 1 ? "Create New Staff" : `New ${role} / Availability`}
        </p>
      </div>

      <div className="flex justify-center p-8">
        {step === 1 && (
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
                onClick={() => navigate("/staff")}
                className="border border-gray-200 text-gray-600 text-sm font-bold px-4 py-2.5 rounded-lg hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={handleContinue}
                disabled={!formValid}
                className={`text-sm font-bold px-4 py-2.5 rounded-lg text-white ${
                  formValid ? "bg-indigo-600 hover:bg-indigo-700" : "bg-indigo-300 cursor-not-allowed"
                }`}
              >
                {role === "Technician" ? "Continue → Set Availability" : "Create Account"}
              </button>
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="w-[460px] bg-white rounded-xl shadow-sm border-2 border-indigo-600 p-6">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-1">
              Set Availability Schedule
            </div>
            <p className="text-xs text-gray-500 mb-4">
              {fullName || "New Technician"} · Technician — {selectedDays.length} consecutive
              working days, daily window between 08:00–20:00
            </p>

            <div className="flex gap-2 mb-6">
              {DAYS.map((day) => {
                const isWeekend = day === "Sat" || day === "Sun";
                const isSelected = selectedDays.includes(day);
                return (
                  <button
                    key={day}
                    disabled={isWeekend}
                    onClick={() => toggleDay(day)}
                    className={`flex-1 text-xs font-bold py-2 rounded-lg ${
                      isWeekend
                        ? "bg-gray-100 text-gray-300 cursor-not-allowed"
                        : isSelected
                        ? "bg-indigo-600 text-white"
                        : "bg-gray-100 text-gray-500"
                    }`}
                  >
                    {day}
                  </button>
                );
              })}
            </div>

            <div className="text-[10px] font-bold uppercase text-gray-400 mb-3">
              Daily Working Hours
            </div>
            <div className="flex items-center gap-3 mb-2">
              <span className="text-xs font-bold text-gray-600">
                {String(startHour).padStart(2, "0")}:00
              </span>
              <input
                type="range"
                min={0}
                max={endHour - 1}
                value={startHour}
                onChange={(e) => setStartHour(Number(e.target.value))}
                className="flex-1 accent-indigo-600"
              />
              <input
                type="range"
                min={startHour + 1}
                max={24}
                value={endHour}
                onChange={(e) => setEndHour(Number(e.target.value))}
                className="flex-1 accent-indigo-600"
              />
              <span className="text-xs font-bold text-gray-600">
                {String(endHour).padStart(2, "0")}:00
              </span>
            </div>
            <p className="text-xs text-gray-400 mb-6">
              Selected: {String(startHour).padStart(2, "0")}:00 – {String(endHour).padStart(2, "0")}:00,
              {" "}
              {selectedDays[0]}–{selectedDays[selectedDays.length - 1]} ({selectedDays.length}{" "}
              consecutive days)
            </p>

            <div className="flex justify-end gap-2.5">
              <button
                onClick={() => setStep(1)}
                className="border border-gray-200 text-gray-600 text-sm font-bold px-4 py-2.5 rounded-lg hover:bg-gray-50"
              >
                ← Back
              </button>
              <button
                onClick={handleCreateAccount}
                className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold px-4 py-2.5 rounded-lg"
              >
                Create Account
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
