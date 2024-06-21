INSERT INTO public.person (name, age) VALUES ('John', 25), ('Mary', 23), ('Alex', 22), ('Bob', 34), ('Joe', 28);

INSERT INTO public.skill (name) VALUES ('java'), ('python'), ('ruby'), ('spring boot'), ('angular'), ('postgresql');

INSERT INTO public.person_skill (person_id, skill_id) VALUES
	(1, 1), (1, 4), (1, 6),
	(2, 2), (2, 3),
	(3, 4), (3, 5),
	(4, 1), (4, 2), (4, 4), (4, 6),
	(5, 2), (5, 5);
