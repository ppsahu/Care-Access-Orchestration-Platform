alter table booking drop constraint if exists booking_slot_id_key;
create unique index if not exists idx_booking_active_slot on booking(slot_id) where cancelled=false;

create table idempotency_record (
  id uuid primary key,
  patient_id varchar(120) not null,
  idempotency_key varchar(128) not null,
  request_hash varchar(64) not null,
  booking_id uuid not null references booking(id),
  created_at timestamptz not null,
  constraint uk_idempotency_scope_key unique(patient_id,idempotency_key)
);
create index idx_idempotency_created_at on idempotency_record(created_at);
