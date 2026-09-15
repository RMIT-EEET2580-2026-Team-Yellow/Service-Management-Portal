package com.fixinh.smt.modules.servicetype.service.external;

import com.fixinh.smt.modules.servicetype.dto.external.ServiceTypeRef;

import java.util.List;

/** External Interface (A.3.1/A.3.2) — consumed by {@code integration.sbp.RealSbpClient} to
 *  enumerate the service types it needs for SBP's per-id rating-stats endpoint (4.E). */
public interface ServiceTypeExternalService {

    List<String> listNames();

    /** id + name for every Service Type — the integration layer needs the id to query SBP
     *  and the name to label the row (3.4.U1). */
    List<ServiceTypeRef> listRefs();
}
