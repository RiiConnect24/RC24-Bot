create table settings
(
  guild_id     bigint not null,
  modlog_id    bigint null,
  serverlog_id bigint null,
  birthdays_id bigint null,
  default_add  int    null,
  prefixes     text   null,
  constraint settings_guild_id_uindex
    unique (guild_id)
);

alter table settings
  add primary key (guild_id);