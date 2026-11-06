package com.ppsahu.careaccess.api;

import com.ppsahu.careaccess.domain.*;
import com.ppsahu.careaccess.service.CareService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.*;

@RestController @RequestMapping("/api/v1")
public class CareController {
    private final CareService care;
    public CareController(CareService care){this.care=care;}
    @GetMapping("/providers")
    public List<AppointmentSlot> search(@RequestParam @NotBlank String specialty,@RequestParam @NotBlank String postalCode,
      @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
      @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to){return care.search(specialty,postalCode,from,to);}
    @PostMapping("/bookings") @ResponseStatus(HttpStatus.CREATED)
    public Booking book(@RequestHeader("X-Demo-Patient") String patientId,@RequestHeader("Idempotency-Key") String key,@RequestBody BookRequest request){
        try{return care.book(patientId,request.slotId(),key);} catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"Slot is no longer available");} catch(CareService.IdempotencyConflictException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"Idempotency key was already used for a different request");} catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}
    }
    @GetMapping("/bookings") public List<Booking> bookings(@RequestHeader("X-Demo-Patient") String patientId){return care.bookings(patientId);}
    @PostMapping("/bookings/{id}/cancel") public Booking cancel(@RequestHeader("X-Demo-Patient") String patientId,@PathVariable UUID id){
        try{return care.cancel(patientId,id);} catch(SecurityException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
    }
    @PostMapping("/bookings/{id}/reschedule") public Booking reschedule(@RequestHeader("X-Demo-Patient") String patientId,@PathVariable UUID id,@RequestHeader("Idempotency-Key") String key,@RequestBody BookRequest request){
        try{return care.reschedule(patientId,id,request.slotId(),key);}catch(CareService.IdempotencyConflictException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"Idempotency key was already used for a different request");}catch(NoSuchElementException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND);}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}
    }
    public record BookRequest(UUID slotId){}
}
