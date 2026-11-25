-- Marker only. Domain tables arrive in later migrations; Hibernate must not create them.
create table schema_baseline (
    id integer primary key,
    note text not null
);

insert into schema_baseline (id, note)
values (1, 'phase 1 baseline');
