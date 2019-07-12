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
