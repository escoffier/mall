
drop database if exists order_db;
create database order_db;

USE orderdb;

drop table if exists orders;
CREATE TABLE orders (
  id          int not null auto_increment,
  user_id     int not null ,
  amount      int not null ,
  total_price float,
  address     varchar(128) not null ,
  create_date datetime,
  status      varchar(16) not null ,
  foreign key (user_id)  references users (id) on delete cascade ,
  PRIMARY KEY (id)
);


drop table if exists users;
create table users (
  id          int not null auto_increment,
  name        varchar(64) not null ,
  password    varchar(64) not null ,
  create_date date,
  primary key (id)
)