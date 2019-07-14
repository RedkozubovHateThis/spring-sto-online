CREATE TABLE public.ref_car
(
  id  serial ,
  title varchar(100)
,  PRIMARY KEY ( id  )
 );
CREATE TABLE public.ref_role
(
  id  serial ,
  title varchar(100)
,  PRIMARY KEY ( id  )
 );
CREATE TABLE public.ref_user
(
  id  serial ,
  first_name varchar(64) ,
  middle_name varchar(64) ,
  last_name varchar(64) ,
  username varchar(32) ,
  password varchar(255) ,
  token varchar(30) ,
  email varchar(255) ,
  created_at timestamp without time zone DEFAULT now(),
  updated_at timestamp without time zone DEFAULT now()
,  PRIMARY KEY ( id  )
 );
CREATE TABLE public.ref_car_state
(
  id  serial ,
  title varchar(100)
,  PRIMARY KEY ( id  )
 );
CREATE TABLE public.reg_order
(
  id  serial ,
  number varchar(100) ,
  start_date date,
  end_date date,
  state smallint,
  client varchar(255) ,
  car varchar(255) ,
  sum real,
  balance real
,  PRIMARY KEY ( id  )
 );
CREATE OR REPLACE FUNCTION trigger_set_timestamp_updated_at()
 RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at= NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;CREATE TRIGGER set_timestamp
BEFORE UPDATE ON ref_user FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp_updated_at();CREATE OR REPLACE FUNCTION trigger_set_timestamp_updated_at()
 RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at= NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;CREATE TRIGGER set_timestamp
BEFORE UPDATE ON doc_order FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp_updated_at();


