package com.fixinh.smt.modules.technician.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Document(collection = "technician_availability_periods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianAvailabilityPeriod {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String technicianId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Builder.Default
    private boolean active = true;
}