-- Refer Wiki for motivation behind data denormalization

CREATE TABLE IF NOT EXISTS people AS
SELECT nconst AS personid,primaryname AS name,birthyear,deathyear,primaryprofession AS professions 
FROM name_basics;

CREATE TABLE IF NOT EXISTS movies AS
SELECT titles.tconst AS movieid,titles.primarytitle AS title,titles.isadult,titles.startyear AS releasedyear,
titles.runtimeminutes,titles.genres,ratings.averagerating,ratings.numvotes FROM (
	SELECT tconst,averagerating,numvotes FROM title_ratings
) as ratings FULL OUTER JOIN (
	SELECT tconst,primarytitle,isadult,startyear,runtimeminutes,genres FROM title_basics
) AS titles ON titles.tconst = ratings.tconst;

CREATE VIEW crew AS
SELECT tconst,nconst,category FROM (
	SELECT tconst,regexp_split_to_table(directors, E',') AS nconst,'director' AS category FROM title_crew UNION
	SELECT tconst,regexp_split_to_table(writers, E',') AS nconst,'writer' AS category FROM title_crew 
) AS nested WHERE nconst IN (
	SELECT crew.nconst FROM (
		SELECT regexp_split_to_table(directors, E',') AS nconst FROM title_crew
	) AS crew INNER JOIN (
		SELECT nconst FROM name_basics
	) AS people ON crew.nconst = people.nconst 
        UNION
	SELECT crew.nconst FROM (
		SELECT regexp_split_to_table(writers, E',') AS nconst FROM title_crew
	) AS crew INNER JOIN (
		SELECT nconst FROM name_basics
	) AS people ON crew.nconst = people.nconst
);

CREATE TABLE consolidated_crew AS
SELECT tconst AS movieid,nconst AS personid,category AS role,job,characters  FROM (
	SELECT tconst,nconst,category,job,characters FROM title_principals
	UNION ALL
	SELECT tconst,nconst,category,null AS job, null AS characters FROM crew AS t2
	WHERE NOT EXISTS (SELECT 1 FROM title_principals AS t1 
					  WHERE t1.tconst=t2.tconst AND t1.nconst=t2.nconst AND t1.category=t2.category)
) AS nested;

CREATE VIEW known_for AS
SELECT * FROM (
	SELECT regexp_split_to_table(knownfortitles, E',') AS tconst,nconst FROM name_basics
) AS nested WHERE tconst IN (
	SELECT knownfor.tconst FROM (
		SELECT regexp_split_to_table(knownfortitles, E',') AS tconst FROM name_basics 
	) as knownfor INNER JOIN (
		SELECT tconst FROM title_basics
	) AS movies ON knownfor.tconst = movies.tconst
);

CREATE TABLE IF NOT EXISTS also_known_for AS
SELECT knownfor.tconst AS movieid,knownfor.nconst AS personid FROM (
	SELECT tconst,nconst FROM known_for
) as knownfor LEFT JOIN (
	SELECT tconst,nconst FROM title_principals
) AS movies ON knownfor.tconst = movies.tconst WHERE movies.tconst ISNULL;
