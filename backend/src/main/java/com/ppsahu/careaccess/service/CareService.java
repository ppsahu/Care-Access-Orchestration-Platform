package com.ppsahu.careaccess.service;

import com.ppsahu.careaccess.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class CareService {
    private final SlotRepository slots; private final BookingRepository bookings; private final IdempotencyRepository idempotency;
    public CareService(SlotRepository slots, BookingRepository bookings,IdempotencyRepository idempotency){this.slots=slots;this.bookings=bookings;this.idempotency=idempotency;}
    public List<AppointmentSlot> search(String specialty,String postalCode,OffsetDateTime from,OffsetDateTime to){
        return slots.findByStatusAndSpecialtyIgnoreCaseAndPostalCodeAndStartsAtBetweenOrderByStartsAt(SlotStatus.OPEN,specialty,postalCode,from,to);
    }
    @Transactional
    public Booking book(String patientId,UUID slotId,String key){
        if(key==null||key.isBlank()||key.length()>128) throw new IllegalArgumentException("A valid Idempotency-Key header is required");
        String hash=hash(slotId.toString());
        var prior=idempotency.findByPatientIdAndIdempotencyKey(patientId,key);
        if(prior.isPresent()){
            if(!prior.get().getRequestHash().equals(hash)) throw new IdempotencyConflictException();
            return bookings.findByIdAndPatientId(prior.get().getBookingId(),patientId).orElseThrow();
        }
        AppointmentSlot slot=slots.lockById(slotId).orElseThrow(()->new NoSuchElementException("Slot not found"));
        slot.book(); // Always re-check authoritative availability inside the transaction.
        Booking saved=bookings.save(new Booking(patientId,slot));
        idempotency.save(new IdempotencyRecord(patientId,key,hash,saved.getId()));
        return saved;
    }
    @Transactional
    public Booking cancel(String patientId,UUID bookingId){
        Booking b=bookings.findById(bookingId).orElseThrow(()->new NoSuchElementException("Booking not found"));
        if(!b.getPatientId().equals(patientId)) throw new SecurityException("Booking not found");
        if(!b.isCancelled()) b.cancel(); return b;
    }
    public List<Booking> bookings(String patientId){return bookings.findByPatientIdOrderByCreatedAtDesc(patientId);}

    @Transactional
    public Booking reschedule(String patientId,UUID bookingId,UUID targetSlotId,String key){
        if(key==null||key.isBlank()||key.length()>128) throw new IllegalArgumentException("A valid Idempotency-Key header is required");
        String hash=hash(bookingId+":"+targetSlotId);
        var prior=idempotency.findByPatientIdAndIdempotencyKey(patientId,key);
        if(prior.isPresent()){
            if(!prior.get().getRequestHash().equals(hash)) throw new IdempotencyConflictException();
            return bookings.findByIdAndPatientId(prior.get().getBookingId(),patientId).orElseThrow();
        }
        Booking current=bookings.findByIdAndPatientId(bookingId,patientId).orElseThrow(()->new NoSuchElementException("Booking not found"));
        if(current.isCancelled()) throw new IllegalStateException("Booking is already cancelled");
        if(current.getSlot().getId().equals(targetSlotId)) throw new IllegalArgumentException("Choose a different appointment slot");
        List<UUID> lockOrder=new ArrayList<>(List.of(current.getSlot().getId(),targetSlotId));
        lockOrder.sort(Comparator.comparing(UUID::toString));
        Map<UUID,AppointmentSlot> locked=new HashMap<>();
        for(UUID id:lockOrder) locked.put(id,slots.lockById(id).orElseThrow(()->new NoSuchElementException("Slot not found")));
        AppointmentSlot target=locked.get(targetSlotId); target.book();
        current.cancel();
        Booking saved=bookings.save(new Booking(patientId,target));
        idempotency.save(new IdempotencyRecord(patientId,key,hash,saved.getId()));
        return saved;
    }
    private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
    public static class IdempotencyConflictException extends RuntimeException {}
}
