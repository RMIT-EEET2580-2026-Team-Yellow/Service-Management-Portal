/**
 * Staff bounded context: Receptionist/Technician account creation, province-based sharding, and
 * Staff search (SRS 3.1.S1-S4, 3.1.M1-M5, 3.1.U1-U2, 3.5.M2, 4.M4). Exposes
 * {@code StaffExternalService} as the ONLY way the Auth module and the Admin/SBT integration
 * reach Staff data (A.2.4).
 */
package com.fixinh.smt.modules.staff;
