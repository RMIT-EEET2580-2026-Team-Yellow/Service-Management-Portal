import { passwordChecks } from "../staffValidation";


function CheckItem({ ok, label }) {
  return (
    <div className={`text-xs flex items-center gap-1.5 ${ok ? "text-green-600" : "text-gray-400"}`}>
      <span>{ok ? "✓" : "○"}</span>
      <span>{label}</span>
    </div>
  );
}

export default function PasswordFields({
  password,
  setPassword,
  confirmPassword,
  setConfirmPassword,
  confirmValid,
}) {
  const checks = passwordChecks(password);

  return (
    <>
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
    </>
  );
}
