package com.fixinh.smt.modules.province.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * One of Vietnam's 34 provinces/cities (post 01/07/2025 merger). {@code region} is the shard
 * region (Northern/Southern) used by {@link com.fixinh.smt.common.sharding.ShardKeyResolver} —
 * boundary is Quảng Trị (Northern, includes former Quảng Bình) / Huế (Southern), per 3.1.U1/U2.
 */
@Document(collection = "provinces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Province {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String name;

    private String region; 
}
