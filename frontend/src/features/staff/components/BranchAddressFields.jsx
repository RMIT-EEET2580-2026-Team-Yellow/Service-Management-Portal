export default function BranchAddressFields({
  streetNumber,
  setStreetNumber,
  streetName,
  setStreetName,
  ward,
  setWard,
  province,
  setProvince,
  provinces,
}) {
  return (
    <>
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
            <option value="">Select province</option>
            {provinces.map((p) => (
              <option key={p.id} value={p.name}>
                {p.name} ({p.region})
              </option>
            ))}
          </select>
        </div>
      </div>
    </>
  );
}
