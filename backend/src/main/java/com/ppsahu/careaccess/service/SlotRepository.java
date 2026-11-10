package com.ppsahu.careaccess.service;

import com.ppsahu.careaccess.domain.AppointmentSlot;
import com.ppsahu.careaccess.domain.SlotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.*;
import java.time.OffsetDateTime;

public interface SlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    Page<AppointmentSlot> findByStatusAndSpecialtyIgnoreCaseAndPostalCodeAndStartsAtBetweenOrderByStartsAt(SlotStatus status, String specialty, String postalCode, OffsetDateTime from, OffsetDateTime to, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from AppointmentSlot s where s.id=:id") Optional<AppointmentSlot> lockById(@Param("id") UUID id);
}
