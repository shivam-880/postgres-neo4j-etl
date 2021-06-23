DROP TABLE IF EXISTS movies_minimal;
DROP TABLE IF EXISTS consolidated_crew_minimal;
DROP TABLE IF EXISTS people_minimal;
DROP TABLE IF EXISTS also_known_for_minimal;

CREATE TABLE movies_minimal AS
SELECT * FROM movies LIMIT :MINIMAL_DATASET_SIZE;

CREATE TABLE consolidated_crew_minimal AS
SELECT t1.movieid,t1.personid,t1.role,t1.job,t1.characters 
FROM consolidated_crew AS t1 INNER JOIN movies_minimal AS t2 ON t1.movieid=t2.movieid;

CREATE TABLE people_minimal AS
SELECT DISTINCT ON (t1.personid) t1.personid,t1.name,t1.birthyear,t1.deathyear,t1.professions 
FROM people AS t1 INNER JOIN consolidated_crew_minimal AS t2 ON t1.personid=t2.personid;

CREATE TABLE also_known_for_minimal AS
SELECT t1.movieid,t1.personid 
FROM also_known_for AS t1 INNER JOIN people_minimal t2 ON t1.personid=t2.personid;

INSERT INTO movies_minimal
SELECT DISTINCT ON (t.movieid) t.movieid,t.title,t.isadult,t.releasedyear,t.runtimeminutes,t.genres,t.averagerating,t.numvotes
FROM movies AS t WHERE movieid in (
	SELECT movieid FROM also_known_for_minimal
);
