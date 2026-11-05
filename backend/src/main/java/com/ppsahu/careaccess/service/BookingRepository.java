package com.ppsahu.careaccess.service;

import com.ppsahu.careaccess.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, UUID> { List<Booking> findByPatientIdOrderByCreatedAtDesc(String patientId); }
