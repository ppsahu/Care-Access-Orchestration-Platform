create table appointment_slot (
  id uuid primary key,
  provider_name varchar(160) not null,
  specialty varchar(80) not null,
  clinic_name varchar(160) not null,
  postal_code varchar(16) not null,
  starts_at timestamptz not null,
  ends_at timestamptz not null,
  status varchar(16) not null check (status in ('OPEN','BOOKED')),
  version bigint not null default 0,
  check (ends_at > starts_at)
);
create index idx_slot_search on appointment_slot(specialty, postal_code, starts_at) where status='OPEN';
create table booking (
  id uuid primary key,
  patient_id varchar(120) not null,
  slot_id uuid not null unique references appointment_slot(id),
  created_at timestamptz not null,
  cancelled boolean not null default false
);
create index idx_booking_patient on booking(patient_id, created_at desc);

insert into appointment_slot(id,provider_name,specialty,clinic_name,postal_code,starts_at,ends_at,status) values
('a1000000-0000-4000-8000-000000000001','Dr. Maya Chen','CARDIOLOGY','Riverside Heart Clinic','02139','2026-12-03T09:00:00-05:00','2026-12-03T09:30:00-05:00','OPEN'),
('a1000000-0000-4000-8000-000000000002','Dr. Maya Chen','CARDIOLOGY','Riverside Heart Clinic','02139','2026-12-04T13:30:00-05:00','2026-12-04T14:00:00-05:00','OPEN'),
('a1000000-0000-4000-8000-000000000003','Dr. Luis Patel','CARDIOLOGY','Central Medical Center','02139','2026-12-08T10:00:00-05:00','2026-12-08T10:30:00-05:00','OPEN'),
('a1000000-0000-4000-8000-000000000004','Dr. Nina Shah','DERMATOLOGY','Riverside Specialty Clinic','02139','2026-12-10T11:00:00-05:00','2026-12-10T11:30:00-05:00','OPEN');
