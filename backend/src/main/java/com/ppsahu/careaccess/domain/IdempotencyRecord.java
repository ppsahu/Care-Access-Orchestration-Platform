package com.ppsahu.careaccess.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="idempotency_record", uniqueConstraints=@UniqueConstraint(name="uk_idempotency_scope_key", columnNames={"patient_id","idempotency_key"}))
public class IdempotencyRecord {
    @Id private UUID id;
    @Column(name="patient_id", nullable=false) private String patientId;
    @Column(name="idempotency_key", nullable=false, length=128) private String idempotencyKey;
    @Column(name="request_hash", nullable=false, length=64) private String requestHash;
    @Column(name="booking_id", nullable=false) private UUID bookingId;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt;
    protected IdempotencyRecord() {}
    public IdempotencyRecord(String patientId,String key,String hash,UUID bookingId){this.id=UUID.randomUUID();this.patientId=patientId;this.idempotencyKey=key;this.requestHash=hash;this.bookingId=bookingId;this.createdAt=OffsetDateTime.now();}
    public String getRequestHash(){return requestHash;} public UUID getBookingId(){return bookingId;}
}
