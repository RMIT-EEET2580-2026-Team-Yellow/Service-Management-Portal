import { useEffect, useState } from "react";
import fetchProvinces from "../services/provinceApi";
import { useStaff } from "../../../common/hooks/useStaff";
import { toDisplayStaff } from "../staffHelpers";
/**
 * SMT is READ-ONLY for Staff — SBT creates Staff accounts, SMT only fetches and
 * displays the list here via GET /api/v1/staff (3.1.S5-S8). SMT still exposes
 * POST /api/v1/staff on the backend for SBT to call, but there is no "Create Staff"
 * UI on this side.
 */

const ROLE_STYLES = {
  Receptionist: "bg-blue-100 text-blue-700",
  Technician: "bg-amber-100 text-amber-700",
  Admin: "bg-purple-100 text-purple-700",
};

function matchesSearch(staff, query) {
  if (!query.trim()) return true;
  const q = query.trim().toLowerCase();
  return (
    staff.name.toLowerCase().includes(q) ||
    staff.id.toLowerCase().includes(q) ||
    staff.email.toLowerCase().includes(q)
  );
}

export default function StaffListPage() {
  const { list, listLoading, listError, loadStaff } = useStaff();
  const [searchText, setSearchText] = useState("");
  const [roleFilter, setRoleFilter] = useState("");
  const [provinceFilter, setProvinceFilter] = useState("");

  const [provinces, setProvinces] = useState([]);

  const fetchProvinceData = async () => {
    const data = await fetchProvinces();
    setProvinces(data);
  };

  useEffect(() => {
    fetchProvinceData();
    loadStaff();
  }, [loadStaff]);

  const staffList = list.map(toDisplayStaff);

  const filteredStaff = staffList
    .filter((s) => matchesSearch(s, searchText))
    .filter((s) => !roleFilter || s.role === roleFilter)
    .filter((s) => !provinceFilter || s.province === provinceFilter);

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4 flex items-center justify-between">
        <div>
          <h1 className="text-lg font-extrabold text-gray-900">
            Staff Accounts
          </h1>
          <p className="text-xs text-gray-400 mt-0.5">Admin / All Staff</p>
        </div>
      </div>

      <div className="p-8">
        <div className="flex gap-2.5 mb-4">
          <input
            placeholder="🔍 Search by name, ID, or email…"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="flex-1 text-sm border border-gray-200 rounded-lg px-3 py-2.5 text-gray-700 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="text-sm font-bold text-indigo-600 bg-white border border-gray-200 rounded-lg px-4 py-2.5 whitespace-nowrap cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Role: All</option>
            <option value="Receptionist">Receptionist</option>
            <option value="Technician">Technician</option>
          </select>
          <select
            value={provinceFilter}
            onChange={(e) => setProvinceFilter(e.target.value)}
            className="text-sm font-bold text-indigo-600 bg-white border border-gray-200 rounded-lg px-4 py-2.5 whitespace-nowrap cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Province: All</option>
            {provinces.map((p) => (
              <option key={p.id} value={p.name}>
                {p.name} - {p.region}
              </option>
            ))}
          </select>
        </div>

        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 text-[11px] uppercase tracking-wide text-gray-400">
                <th className="text-left font-bold px-4 py-3">Staff ID</th>
                <th className="text-left font-bold px-4 py-3">Full Name</th>
                <th className="text-left font-bold px-4 py-3">Phone</th>
                <th className="text-left font-bold px-4 py-3">Email</th>
                <th className="text-left font-bold px-4 py-3">Role</th>
                <th className="text-left font-bold px-4 py-3">Branch / Province</th>
                <th className="text-left font-bold px-4 py-3">Status</th>
              </tr>
            </thead>
            <tbody>
              {listLoading && (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-sm text-gray-400">
                    Loading staff…
                  </td>
                </tr>
              )}
              {!listLoading && listError && (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-sm text-red-500">
                    {listError}
                  </td>
                </tr>
              )}
              {!listLoading && !listError && filteredStaff.length === 0 && (
                <tr>
                  <td
                    colSpan={7}
                    className="px-4 py-8 text-center text-sm text-gray-400"
                  >
                    No staff match &quot;{searchText}&quot;.
                  </td>
                </tr>
              )}
              {!listLoading && !listError && filteredStaff.map((s) => (
                <tr
                  key={s.id}
                  className="border-t border-gray-100 hover:bg-gray-50"
                >
                  <td className="px-4 py-3.5 font-bold text-gray-900">
                    {s.id}
                  </td>
                  <td className="px-4 py-3.5">{s.name}</td>
                  <td className="px-4 py-3.5 text-gray-500">{s.phone}</td>
                  <td className="px-4 py-3.5 text-gray-500">{s.email}</td>
                  <td className="px-4 py-3.5">
                    <span
                      className={`text-xs font-semibold px-2.5 py-1 rounded-full whitespace-nowrap ${ROLE_STYLES[s.role]}`}
                    >
                      {s.role}
                    </span>
                  </td>
                  <td className="px-4 py-3.5 text-gray-500">{s.province}</td>
                  <td className="px-4 py-3.5">
                    <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-green-100 text-green-700">
                      {s.status}
                    </span>
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
