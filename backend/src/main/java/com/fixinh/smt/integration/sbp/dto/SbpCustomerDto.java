// Path in repo: backend/src/main/java/com/fixinh/smt/integration/sbp/dto/SbpCustomerDto.java
// CHANGE: customerId Long -> String (UUID), per 30/7 agreement with SBT.
package com.fixinh.smt.integration.sbp.dto;

/** Shape of the Customer data SBP exposes to SMP (4.M1). Read-only from SMT's side. */
public record SbpCustomerDto(String customerId, String fullName, String phoneNumber) {
}
