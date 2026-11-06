package com.ppsahu.careaccess.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {
    @Id private UUID id;
    private String patientId;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="slot_id", nullable=false)
    private AppointmentSlot slot;
    private OffsetDateTime createdAt;
    private boolean cancelled;
    protected Booking() {}
    public Booking(String patientId, AppointmentSlot slot) { this.id=UUID.randomUUID(); this.patientId=patientId; this.slot=slot; this.createdAt=OffsetDateTime.now(); }
    public UUID getId(){return id;} public String getPatientId(){return patientId;} public AppointmentSlot getSlot(){return slot;}
    public OffsetDateTime getCreatedAt(){return createdAt;} public boolean isCancelled(){return cancelled;}
    public void cancel(){cancelled=true; slot.release();}
}
