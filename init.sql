create table movie (
	id serial primary KEY NOT NULL,
	title varchar(100),
	description varchar(1000)
);

create table place (
	id serial primary KEY NOT NULL,
	"number" varchar(5)
);

create table "session" (
	id serial primary KEY NOT NULL,
	movie_id bigint,
	date_and_time timestamp,
	price numeric,
	foreign key (movie_id) references movie (id)
);

create table ticket (
	id serial primary KEY NOT NULL,
	place_id bigint,
	foreign key (place_id) references place (id),
	session_id bigint,
	foreign key (session_id) references "session" (id),
	is_buy boolean
);
