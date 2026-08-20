export function validateFullName(fullName) {
  const nameRegex = /^[A-Z][a-z]+$/;
  if (!fullName || fullName.trim() === "") {
    return "Full name is required";
  }
  if (!nameRegex.test(fullName.trim())) {
    return "Each word in the full name must start with a capital letter ";
  }
  return "";
}

export function validateEmail(email) {
  // SRS 3.1.M2: exactly one '@', at least one '.' after '@', length < 255,
  const emailRegex = /^[^\s@()[\]<>,;:]+@[^\s@()[\]<>,;:]+\.[^\s@()[\]<>,;:]+$/;
  if (!email || email.trim() === "") {
    return "Email is required";
  }
  if (email.length >= 255) {
    return "Email must be less than 255 characters";
  }
  if (!emailRegex.test(email)) {
    return "Invalid email — must contain exactly one \"@\", a \".\" after it, and no spaces or prohibited characters";
  }
  return "";
}

export function validatePhone(phone) {
  const phoneRegex = /^0\d{6,10}$/;
  if (!phone || phone.trim() === "") {
    return "Phone number is required";
  }
  if (!phoneRegex.test(phone)) {
    return "Invalid phone — 7-11 digits, must start with 0";
  }
  return "";
}

export function passwordChecks(password) {
  return {
    length: password.length >= 8,
    number: /\d/.test(password),
    special: /[$#@!]/.test(password),
    capital: /[A-Z]/.test(password),
  };
}

export function validatePassword(password) {
  if (!password) {
    return "Password is required";
  }
  const checks = passwordChecks(password);
  if (!Object.values(checks).every(Boolean)) {
    return "Password must be at least 8 characters, with a number, a special character ($#@!), and a capital letter";
  }
  return "";
}

export function validateConfirmPassword(password, confirmPassword) {
  if (!confirmPassword) {
    return "Please confirm your password";
  }
  if (confirmPassword !== password) {
    return "Passwords do not match";
  }
  return "";
}

export function validateStreetNumber(streetNumber) {
  if (!streetNumber || streetNumber.trim() === "") {
    return "Street number is required";
  }
  return "";
}

export function validateStreetName(streetName) {
  if (!streetName || streetName.trim() === "") {
    return "Street name is required";
  }
  return "";
}

export function validateWard(ward) {
  if (!ward || ward.trim() === "") {
    return "Ward is required";
  }
  return "";
}

export function validateProvince(province) {
  if (!province || province.trim() === "") {
    return "Province is required";
  }
  return "";
}

export default function validateNewStaff(staff) {
  const validatorResult = {
    fullName: validateFullName(staff.fullName),
    email: validateEmail(staff.email),
    phone: validatePhone(staff.phone),
    password: validatePassword(staff.password),
    confirmPassword: validateConfirmPassword(staff.password, staff.confirmPassword),
    streetNumber: validateStreetNumber(staff.streetNumber),
    streetName: validateStreetName(staff.streetName),
    ward: validateWard(staff.ward),
    province: validateProvince(staff.province),
  };
  const isValid = Object.values(validatorResult).every((msg) => msg === "");
  return { ...validatorResult, isValid };
}
