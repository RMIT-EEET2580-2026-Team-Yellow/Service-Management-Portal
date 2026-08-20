export default function RoleToggle({ role, setRole, setProvince }) {
  return (
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
  );
}
