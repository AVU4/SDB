drop table if exists moex;
create table moex(
  DATE bigint,
  TITLE text,
  SUMMARY text,
  INDEX_VALUES double precision[]
);