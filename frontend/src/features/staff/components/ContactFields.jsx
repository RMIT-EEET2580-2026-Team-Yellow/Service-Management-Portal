import { validateEmail } from "../staffValidation";

/**
 * Full Name + Email + Phone inputs, with real-time error feedback.
 * Split out to keep RegisterStaffForm.jsx short.
 */
export default function ContactFields({
  fullName,
  setFullName,
  email,
  setEmail,
  phone,
  setPhone,
  emailValid,
  phoneValid,
}) {
  return (
    <>
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
          ⚠ {email && !emailValid ? validateEmail(email) : "placeholder"}
        </p>
        <p className={`text-xs ${phone ? (phoneValid ? "text-green-600" : "text-red-500") : "text-transparent"}`}>
          {phoneValid ? "✓" : "⚠"} Valid — 7-11 digits, starts with 0
        </p>
      </div>
    </>
  );
}
