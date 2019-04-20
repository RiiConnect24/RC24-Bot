create table codes
(
  user_id bigint     not null,
  games   mediumtext null,
  nnid    mediumtext null,
  switch  mediumtext null,
  threeds mediumtext null,
  wii     mediumtext null,
  psn     mediumtext null,
  constraint codes_user_id_uindex
    unique (user_id)
);

alter table codes
  add primary key (user_id);