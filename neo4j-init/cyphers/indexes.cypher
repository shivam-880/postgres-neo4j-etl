CREATE INDEX year_index IF NOT EXISTS FOR (y:Year) ON (y.year)

CREATE INDEX person_name_index IF NOT EXISTS FOR (p:Person) ON (p.name)

CREATE INDEX movie_title_index IF NOT EXISTS FOR (m:Movie) ON (m.title)

CREATE INDEX genre_type_index IF NOT EXISTS FOR (g:Genre) ON (g.type)
