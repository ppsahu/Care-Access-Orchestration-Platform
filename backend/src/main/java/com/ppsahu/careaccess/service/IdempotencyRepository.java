package com.ppsahu.careaccess.service;

import com.ppsahu.careaccess.domain.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord,UUID> {
    Optional<IdempotencyRecord> findByPatientIdAndIdempotencyKey(String patientId,String idempotencyKey);
}
