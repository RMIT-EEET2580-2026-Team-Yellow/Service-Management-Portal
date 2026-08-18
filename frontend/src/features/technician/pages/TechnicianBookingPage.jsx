import './TechnicianBookingPage.css';

function TechnicianBookingPage() {
  return (
    <>
      <div className="google-sans-flex flex flex-row justify-between w-full bg-red-100 px-4 py-2">
        <div className="w-64 flex-initial flex flex-col justify-start">
          <h1 className="text-lg font-bold">My Bookings</h1>
          <p className="text-sm text-gray-600">Vong Tran Gia - Technician</p>
        </div>

        <div className="w-64 flex-initial flex justify-center content-center">
          <input type="text" 
            placeholder="Search bookings..."
            className="border border-gray-300 rounded px-3 py-2 w-full"
          />
        </div>
      </div>

      <div className="flex flex-row justify-start gap-2 w-full px-4 my-2 bg-blue-100">
        <p className="text-xs text-gray-500 font-bold">Assigned</p>
        <p className="text-xs text-gray-500 font-bold">Working</p>
        <p className="text-xs text-gray-500 font-bold">History</p>
      </div>

      <hr className="mx-4 border-zinc-300" />

      <div className="px-4 bg-red-100 py-2 flex flex-col gap-2">
        <div className="flex-1 w-full bg-white px-2 py-2 flex flex-row justify-between content-center">
          <div>
            This is info container
          </div>
          <div>
            Button container
          </div>
        </div>
      </div>
    </>
  );
}

export default TechnicianBookingPage;