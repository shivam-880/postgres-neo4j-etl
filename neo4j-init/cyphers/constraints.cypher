CREATE CONSTRAINT unique_year_constraint IF NOT EXISTS ON (y:Year) ASSERT y.year IS UNIQUE

CREATE CONSTRAINT unique_genre_constraint IF NOT EXISTS ON (g:Genre) ASSERT g.type IS UNIQUE

CREATE CONSTRAINT unique_personid_constraint IF NOT EXISTS ON (p:Person) ASSERT p.personId IS UNIQUE

CREATE CONSTRAINT unique_movieid_constraint IF NOT EXISTS ON (m:Movie) ASSERT m.movieId IS UNIQUE

CREATE CONSTRAINT unique_movie_title_constraint IF NOT EXISTS ON (m:Movie) ASSERT m.title IS UNIQUE
