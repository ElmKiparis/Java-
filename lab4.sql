DROP TABLE IF EXISTS public.person;

CREATE TABLE IF NOT EXISTS public.person (
	id serial not null primary key,
	name varchar(50) not null,
	age int not null,
	avatar_filename varchar(50)
);
