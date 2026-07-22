package com.fixinh.smt.modules.technician.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.List;

/**
 * Extends a {@code Staff} account (by {@code staffId}) with scheduling data required to
 * auto-assign work (3.1.M5, 3.2.M5, 3.3.M3).
 */
@Document(collection = "technicians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Technician {

    @Id
    private String id;

    private String staffId;

    /** 5 consecutive working days, e.g. ["MON","TUE","WED","THU","FRI"]. */
    private List<String> workingDays;

    private LocalTime dailyStart; // >= 08:00
    private LocalTime dailyEnd;   // <= 20:00

    private boolean currentlyAvailable;
}
