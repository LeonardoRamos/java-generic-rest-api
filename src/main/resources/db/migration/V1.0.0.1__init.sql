CREATE SEQUENCE country_id_seq
  	INCREMENT 1
  	MINVALUE 1
  	MAXVALUE 9223372036854775807
  	START 1
  	CACHE 1;

CREATE SEQUENCE user_account_id_seq
  	INCREMENT 1
  	MINVALUE 1
  	MAXVALUE 9223372036854775807
  	START 1
  	CACHE 1;

CREATE SEQUENCE address_id_seq
  	INCREMENT 1
  	MINVALUE 1
  	MAXVALUE 9223372036854775807
  	START 1
  	CACHE 1;
  	
CREATE TYPE enum_user_account_role AS ENUM('ADMIN', 'USER');

CREATE TABLE country(
	id bigint NOT NULL DEFAULT nextval('country_id_seq'::regclass),
  	external_id character varying(32) NOT NULL,
  	active boolean NOT NULL,
  	creation_date timestamp with time zone NOT NULL,
  	update_date timestamp with time zone NOT NULL,
  	delete_date timestamp with time zone,
  	name character varying(255),
  	CONSTRAINT country_pkey PRIMARY KEY (id),
  	CONSTRAINT country_external_id_key UNIQUE (external_id)
);

CREATE TABLE user_account(
	id bigint NOT NULL DEFAULT nextval('user_account_id_seq'::regclass),
  	external_id character varying(32) NOT NULL,
  	active boolean NOT NULL,
  	creation_date timestamp with time zone NOT NULL,
  	update_date timestamp with time zone NOT NULL,
  	delete_date timestamp with time zone,
  	name character varying(255),
  	email character varying(255) NOT NULL,
  	password character varying(255) NOT NULL,
  	role enum_user_account_role NOT NULL,
  	CONSTRAINT user_account_pkey PRIMARY KEY (id),
  	CONSTRAINT user_account_email_key UNIQUE (email),
  	CONSTRAINT user_account_external_id_key UNIQUE (external_id)
);

CREATE TABLE address(
	id bigint NOT NULL DEFAULT nextval('address_id_seq'::regclass),
  	external_id character varying(32) NOT NULL,
  	active boolean NOT NULL,
  	creation_date timestamp with time zone NOT NULL,
  	update_date timestamp with time zone NOT NULL,
  	delete_date timestamp with time zone,
  	street character varying(255),
  	street_number character varying(255),
  	state character varying(255),
  	id_country bigint,
  	id_user bigint,
  	CONSTRAINT address_pkey PRIMARY KEY (id),
  	CONSTRAINT address_id_country_fkey FOREIGN KEY (id_country)
  		REFERENCES country (id) MATCH SIMPLE
      	ON UPDATE CASCADE ON DELETE SET NULL,
  	CONSTRAINT address_id_user_fkey FOREIGN KEY (id_user)
      	REFERENCES user_account (id) MATCH SIMPLE
      	ON UPDATE CASCADE ON DELETE CASCADE,
  	CONSTRAINT address_external_id_key UNIQUE (external_id)
);