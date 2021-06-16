create table coordinates
(
  lat double precision not null,
  lng double precision not null,
  id  serial           not null
    constraint coordinates_pkey
    primary key
);

alter table coordinates
  owner to postgres;

create unique index coordinates_id_uindex
  on coordinates (id);

--

create table dog_breed
(
  id        serial       not null
    constraint dog_breed_pkey
    primary key,
  name      varchar(255) not null,
  image_src varchar(255),
  name_en   varchar(255) not null
);

alter table dog_breed
  owner to postgres;

create unique index dog_breed_id_uindex
  on dog_breed (id);

create unique index dog_breed_breed_name_uindex
  on dog_breed (name);

create unique index dog_breed_image_src_uindex
  on dog_breed (image_src);

--

create type sex as enum ('Male', 'Female');

alter type sex
  owner to postgres;

--

create table users
(
  id            serial       not null
    constraint users_pk
    primary key,
  email         varchar(255) not null,
  password_hash varchar(255) not null
);

alter table users
  owner to postgres;

create unique index users_email_uindex
  on users (email);

create unique index users_id_uindex
  on users (id);

--

create table dog
(
  name      varchar(255) not null,
  breed_id  integer      not null
    constraint dog_dog_breed_id_fk
    references dog_breed,
  owner_id  integer      not null
    constraint dog_users_id_fk
    references users,
  id        serial       not null
    constraint dog_pk
    primary key,
  sex       sex          not null,
  year_born integer
);

alter table dog
  owner to postgres;

create unique index dog_id_uindex
  on dog (id);

create unique index dog_owner_id_name_uindex
  on dog (owner_id, name);

--

create table routes
(
  id          serial  not null
    constraint routes_pkey
    primary key,
  user_id     integer not null
    constraint routes_users_id_fk
    references users,
  created_at  timestamp,
  is_active   boolean,
  coordinates text    not null,
  length      integer,
  start_id    integer not null
    constraint routes_coordinates_id_fk_2
    references coordinates,
  median_id   integer not null
    constraint routes_coordinates_id_fk
    references coordinates
);

alter table routes
  owner to postgres;

create unique index routes_id_uindex
  on routes (id);

create unique index routes_start_id_uindex
  on routes (start_id);

create unique index routes_median_id_uindex
  on routes (median_id);
