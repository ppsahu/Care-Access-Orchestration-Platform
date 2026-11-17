update appointment_slot set specialty=upper(specialty) where specialty<>upper(specialty);

alter table appointment_slot
  add constraint chk_appointment_slot_specialty_upper
  check (specialty=upper(specialty));
