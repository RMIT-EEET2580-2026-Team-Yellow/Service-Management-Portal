// Pure validation helpers used by StaffCreatePage / RegisterStaffForm.
// No React here — just plain functions, easy to unit test later.

export function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

export function validatePhone(phone) {
  return /^0\d{6,10}$/.test(phone);
}

export function passwordChecks(password) {
  return {
    length: password.length >= 8,
    number: /\d/.test(password),
    special: /[$#@!]/.test(password),
    capital: /[A-Z]/.test(password),
  };
}
