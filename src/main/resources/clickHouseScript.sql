create database IF NOT EXISTS market;
USE market;
DROP TABLE IF EXISTS moex;
CREATE TABLE moex(
    dayId UInt256,
    title String,
    summary String,
    indexValue Float64
);