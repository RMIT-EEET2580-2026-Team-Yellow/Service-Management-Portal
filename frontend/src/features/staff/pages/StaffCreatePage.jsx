import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import fetchProvinces from "../services/provinceApi";
import RegisterStaffForm from "../components/RegisterStaffForm";
import AvailabilityForm from "../components/AvailabilityForm";

/**
 * MOCK UI — Staff create/availability flow is still mock, but the Province
 * dropdown is wired to the real GET /api/v1/provinces backend.
 * Matches SRS 3.1.S1: Admin registers a new Receptionist or Technician with
 * Email, Phone Number, Password, Confirm Password, Branch Address as mandatory
 * fields. Technicians additionally get a working-availability schedule
 * (3.1.M8-style) before the account is created.
 *
 * Step 1 JSX lives in components/RegisterStaffForm.jsx, Step 2 JSX lives in
 * components/AvailabilityForm.jsx — this file only owns the state.
 */

const WORKING_DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

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

  function handleContinue() {
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
          <RegisterStaffForm
            role={role}
            setRole={setRole}
            fullName={fullName}
            setFullName={setFullName}
            email={email}
            setEmail={setEmail}
            phone={phone}
            setPhone={setPhone}
            password={password}
            setPassword={setPassword}
            confirmPassword={confirmPassword}
            setConfirmPassword={setConfirmPassword}
            streetNumber={streetNumber}
            setStreetNumber={setStreetNumber}
            streetName={streetName}
            setStreetName={setStreetName}
            ward={ward}
            setWard={setWard}
            province={province}
            setProvince={setProvince}
            provinces={provinces}
            onCancel={() => navigate("/staff")}
            onContinue={handleContinue}
          />
        )}

        {step === 2 && (
          <AvailabilityForm
            fullName={fullName}
            role={role}
            selectedDays={selectedDays}
            toggleDay={toggleDay}
            startHour={startHour}
            setStartHour={setStartHour}
            endHour={endHour}
            setEndHour={setEndHour}
            onBack={() => setStep(1)}
            onCreateAccount={handleCreateAccount}
          />
        )}
      </div>
    </div>
  );
}
