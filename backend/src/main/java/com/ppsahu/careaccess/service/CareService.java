package com.ppsahu.careaccess.service;

import com.ppsahu.careaccess.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class CareService {
    private final SlotRepository slots; private final BookingRepository bookings;
    public CareService(SlotRepository slots, BookingRepository bookings){this.slots=slots;this.bookings=bookings;}
    public List<AppointmentSlot> search(String specialty,String postalCode,OffsetDateTime from,OffsetDateTime to){
        return slots.findByStatusAndSpecialtyIgnoreCaseAndPostalCodeAndStartsAtBetweenOrderByStartsAt(SlotStatus.OPEN,specialty,postalCode,from,to);
    }
    @Transactional
    public Booking book(String patientId,UUID slotId){
        AppointmentSlot slot=slots.lockById(slotId).orElseThrow(()->new NoSuchElementException("Slot not found"));
        slot.book(); // Eligibility adapters are added in the next slice; never trust cached availability here.
        return bookings.save(new Booking(patientId,slot));
    }
    @Transactional
    public Booking cancel(String patientId,UUID bookingId){
        Booking b=bookings.findById(bookingId).orElseThrow(()->new NoSuchElementException("Booking not found"));
        if(!b.getPatientId().equals(patientId)) throw new SecurityException("Booking not found");
        if(!b.isCancelled()) b.cancel(); return b;
    }
    public List<Booking> bookings(String patientId){return bookings.findByPatientIdOrderByCreatedAtDesc(patientId);}
}
