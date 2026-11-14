package com.ppsahu.careaccess.service;

import com.ppsahu.careaccess.domain.IdempotencyRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord,UUID> {
    @Modifying
    @Query(value="insert into idempotency_record (id,patient_id,idempotency_key,request_hash,booking_id,created_at) values (:id,:patientId,:key,:hash,null,now()) on conflict (patient_id,idempotency_key) do nothing",nativeQuery=true)
    int claimIfAbsent(@Param("id") UUID id,@Param("patientId") String patientId,@Param("key") String key,@Param("hash") String hash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from IdempotencyRecord r where r.patientId=:patientId and r.idempotencyKey=:key")
    Optional<IdempotencyRecord> lockByPatientAndKey(@Param("patientId") String patientId,@Param("key") String key);
}
