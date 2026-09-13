/**
 * Province bounded context: canonical reference list of Vietnam's 34 provinces/cities (post
 * 01/07/2025 merger, Nghị quyết 202/2025/QH15), each tagged Northern/Southern region. Used to
 * populate Province dropdowns in Address forms (3.1.S4) and, via {@link
 * com.fixinh.smt.modules.province.service.external.ProvinceExternalService} (A.2.4/A.3.1), as
 * the source of truth {@link com.fixinh.smt.common.sharding.ShardKeyResolver} reads from when
 * deciding which Mongo shard (Northern/Southern) a Staff record belongs to (3.1.U1/U2).
 */
package com.fixinh.smt.modules.province;
