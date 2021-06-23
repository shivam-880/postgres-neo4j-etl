-- To learn more about imdb datasets head to https://www.imdb.com/interfaces/

CREATE TABLE IF NOT EXISTS name_basics (
    nconst	            VARCHAR(10) CONSTRAINT PK_NAME_BASICS PRIMARY KEY,
    primaryName	        VARCHAR(110),
    birthYear           INTEGER,
    deathYear	        INTEGER,
    primaryProfession   VARCHAR(200),
    knownForTitles      VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS title_basics (
    tconst          VARCHAR(10) CONSTRAINT PK_TITLE_BASICS PRIMARY KEY,
    titleType       VARCHAR(20),
    primaryTitle    VARCHAR(500),
    originalTitle   VARCHAR(500),
    isAdult         BOOLEAN,
    startYear       INTEGER,
    endYear         INTEGER,
    runtimeMinutes  INTEGER,
    genres          VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS title_ratings (
    tconst          VARCHAR(10) CONSTRAINT PK_TITLE_RATINGS PRIMARY KEY,
    averageRating   DOUBLE PRECISION,
    numVotes        INTEGER,
    FOREIGN KEY (tconst) REFERENCES title_basics(tconst)
);

CREATE TABLE IF NOT EXISTS title_principals (
    tconst          VARCHAR(10),
    ordering        INTEGER,
    nconst          VARCHAR(10),
    category        VARCHAR(100),
    job             VARCHAR(300),
    characters      VARCHAR(500),
    PRIMARY KEY (tconst, ordering, nconst),
    FOREIGN KEY (tconst) REFERENCES title_basics(tconst),
    FOREIGN KEY (nconst) REFERENCES name_basics(nconst)
);

CREATE TABLE IF NOT EXISTS title_crew (
    tconst      VARCHAR(10) CONSTRAINT PK_TITLE_CREW PRIMARY KEY,
    directors   VARCHAR(500),
    writers     VARCHAR(500),
    FOREIGN KEY (tconst) REFERENCES title_basics(tconst)
);
