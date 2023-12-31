CREATE SEQUENCE hibernate_sequence  INCREMENT 1  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
CREATE TABLE user_table_aud
(
	acc_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	rev INT,
	revtype SMALLINT,
	name varchar(255) NOT NULL,
	address jsonb,
	email varchar(255) NOT NULL,
	phone varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	role varchar(255),
	is_deleted boolean
);

CREATE TABLE products_aud
(
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	rev INTEGER,
	revtype SMALLINT,
	name varchar(255) NOT NULL,
	category varchar(255),
	price float,
	discount float,
	stock BIGINT,
	userid BIGINT NOT NULL,
	description varchar(255),
	creation_time TIMESTAMP,
	is_deleted boolean
);

CREATE TABLE basket_aud
(
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	rev INTEGER,
	revtype SMALLINT,
	userid varchar(255) NOT NULL,
	totalamount float,
	totaldiscount float,
	productinbasket jsonb
);

CREATE TABLE payments_aud
(
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	rev INTEGER,
	revtype SMALLINT,
	orderid BIGINT,
	fromid varchar(255),
	toid varchar(255),
	amount float,
	type varchar(255),
	status varchar(255),
	comment varchar(255),
	time TIMESTAMP
);

CREATE TABLE wallet_aud
(
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	rev INTEGER,
	revtype SMALLINT,
	userid varchar(255),
	amount float
);

CREATE TABLE grocery_orders_aud
(
	order_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	rev INTEGER,
	revtype SMALLINT,
	buyer_id BIGINT,
	del_info jsonb,
	order_date_time TIMESTAMP,
	prod_list jsonb,
	order_status INTEGER,
	last_processed TIMESTAMP,
	order_amount float
);
CREATE TABLE revinfo
(
	rev INTEGER,
	revtstmp BIGINT
);