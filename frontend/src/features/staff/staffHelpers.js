function toTitleCase(role) {
  if (!role) return "";
  return role.charAt(0) + role.slice(1).toLowerCase();
}

export function toDisplayStaff(staff) {
  return {
    id: staff.id,
    name: staff.fullName,
    phone: staff.phoneNumber,
    email: staff.email,
    role: toTitleCase(staff.role),
    province: staff.province,
    status: staff.active ? "Active" : "Inactive",
  };
}
