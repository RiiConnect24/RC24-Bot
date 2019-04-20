create table birthdays
(
  user_id bigint     not null,
  day     varchar(5) null,
  constraint birthdays_user_id_uindex
    unique (user_id)
);

alter table birthdays
  add primary key (user_id);