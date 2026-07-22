package com.fixinh.smt.integration.sbp.dto;

/** Shape of the Customer data SBP exposes to SMP (4.M1). Read-only from SMT's side. */
public record SbpCustomerDto(Long customerId, String fullName, String phoneNumber) {
}
