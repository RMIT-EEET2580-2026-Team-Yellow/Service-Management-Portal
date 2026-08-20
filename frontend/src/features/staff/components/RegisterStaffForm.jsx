import validateNewStaff, {
  validateEmail,
  validatePhone,
  validateConfirmPassword,
} from "../staffValidation";
import RoleToggle from "./RoleToggle";
import ContactFields from "./ContactFields";
import PasswordFields from "./PasswordFields";
import BranchAddressFields from "./BranchAddressFields";

/**
 * MOCK UI — Step 1 of Staff creation (SRS 3.1.S1). Split out of
 * StaffCreatePage.jsx to keep that file short; all state still lives in the
 * parent, this component only computes validity and renders the 4 section
 * components (RoleToggle, ContactFields, PasswordFields, BranchAddressFields).
 */
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
  const emailValid = email === "" || validateEmail(email) === "";
  const phoneValid = phone === "" || validatePhone(phone) === "";
  const confirmValid = confirmPassword === "" || validateConfirmPassword(password, confirmPassword) === "";

  const formValid = validateNewStaff({
    fullName,
    email,
    phone,
    password,
    confirmPassword,
    streetNumber,
    streetName,
    ward,
    province,
  }).isValid;

  return (
    <div className="w-[420px] bg-white rounded-xl shadow-sm p-6">
      <div className="text-[10px] font-bold uppercase text-gray-400 mb-4">
        Register New Staff
      </div>

      <RoleToggle role={role} setRole={setRole} setProvince={setProvince} />

      <ContactFields
        fullName={fullName}
        setFullName={setFullName}
        email={email}
        setEmail={setEmail}
        phone={phone}
        setPhone={setPhone}
        emailValid={emailValid}
        phoneValid={phoneValid}
      />

      <PasswordFields
        password={password}
        setPassword={setPassword}
        confirmPassword={confirmPassword}
        setConfirmPassword={setConfirmPassword}
        confirmValid={confirmValid}
      />

      <div className="border-t border-gray-100 -mx-6 mb-5" />

      <BranchAddressFields
        streetNumber={streetNumber}
        setStreetNumber={setStreetNumber}
        streetName={streetName}
        setStreetName={setStreetName}
        ward={ward}
        setWard={setWard}
        province={province}
        setProvince={setProvince}
        provinces={provinces}
      />

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
