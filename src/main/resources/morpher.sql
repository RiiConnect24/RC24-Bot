create table morpher
(
  root_msg_id   bigint not null
    primary key,
  mirror_msg_id bigint null
);