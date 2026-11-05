package com.ppsahu.careaccess.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_slot")
public class AppointmentSlot {
    @Id private UUID id;
    private String providerName;
    private String specialty;
    private String clinicName;
    private String postalCode;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    @Enumerated(EnumType.STRING) private SlotStatus status;
    @Version private long version;

    protected AppointmentSlot() {}
    public AppointmentSlot(UUID id, String providerName, String specialty, String clinicName, String postalCode,
                          OffsetDateTime startsAt, OffsetDateTime endsAt, SlotStatus status) {
        this.id=id; this.providerName=providerName; this.specialty=specialty; this.clinicName=clinicName;
        this.postalCode=postalCode; this.startsAt=startsAt; this.endsAt=endsAt; this.status=status;
    }
    public UUID getId(){return id;} public String getProviderName(){return providerName;}
    public String getSpecialty(){return specialty;} public String getClinicName(){return clinicName;}
    public String getPostalCode(){return postalCode;} public OffsetDateTime getStartsAt(){return startsAt;}
    public OffsetDateTime getEndsAt(){return endsAt;} public SlotStatus getStatus(){return status;}
    public void book(){if(status!=SlotStatus.OPEN) throw new IllegalStateException("Slot is no longer available"); status=SlotStatus.BOOKED;}
    public void release(){status=SlotStatus.OPEN;}
}
