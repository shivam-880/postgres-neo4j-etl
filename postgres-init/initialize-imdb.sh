#!/usr/bin/env bash

Bold='\033[1m'   # Bold
NC='\033[0m'     # No Color

# Change this banner: http://patorjk.com/software/taag/#p=display&v=1&f=Standard&t=LOADING%0AIMDB%0ADATASETS
cat << "EOF"
  _     ___    _    ____ ___ _   _  ____         
 | |   / _ \  / \  |  _ \_ _| \ | |/ ___|        
 | |  | | | |/ _ \ | | | | ||  \| | |  _         
 | |__| |_| / ___ \| |_| | || |\  | |_| |        
 |_____\___/_/   \_\____/___|_| \_|\____|        
  ___ __  __ ____  ____                          
 |_ _|  \/  |  _ \| __ )                         
  | || |\/| | | | |  _ \                         
  | || |  | | |_| | |_) |                        
 |___|_|  |_|____/|____/                         
  ____    _  _____  _    ____  _____ _____ ____  
 |  _ \  / \|_   _|/ \  / ___|| ____|_   _/ ___| 
 | | | |/ _ \ | | / _ \ \___ \|  _|   | | \___ \ 
 | |_| / ___ \| |/ ___ \ ___) | |___  | |  ___) |
 |____/_/   \_\_/_/   \_\____/|_____| |_| |____/ 
                                                 
 This process will take from 20 to 30 minutes

EOF

if [ "$RELOAD_DATASET" = true ] ; then
    echo -e "${Bold}Updating container and installing packages...${NC}"
    apt-get -y -qq update && apt-get -y -qq install wget && apt-get -y -qq install curl && \
    curl -fLo cs https://git.io/coursier-cli-"$(uname | tr LD ld)" && \
    chmod +x cs && \
    ./cs install cs && \
    rm cs && \
    echo export PATH="$PATH:/root/.local/share/coursier/bin" >> ~/.bashrc && \
    source ~/.bashrc

    echo -e "${Bold}Downloading IMDB datasets...${NC}"
    FILES=('name.basics.tsv' 'title.basics.tsv' 'title.principals.tsv' 'title.ratings.tsv' 'title.crew.tsv')
    for file in "${FILES[@]}"; do
        echo -e "  ${file}"
        WGET_ARGS="-q -O ${file}.gz https://datasets.imdbws.com/${file}.gz"
        echo $WGET_ARGS | xargs wget
    done

    echo -e "${Bold}Decompressing datasets...${NC}"
    for file in "${FILES[@]}"; do
        gzip -d "${file}.gz"
    done

    ./data-cleanup.sh

    echo -e "${Bold}Initializing schema...${NC}"
    psql --host=postgres --username=postgres -d imdb -f ./schema.sql

    echo -e "${Bold}Loading Name Basics...${NC}"
    psql --host=postgres --username=postgres -d imdb -c "\copy name_basics(nconst,primaryname,birthyear,deathyear,primaryprofession,knownfortitles) FROM 'name.basics.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER"

    echo -e "${Bold}Loading Title Basics...${NC}"
    psql --host=postgres --username=postgres -d imdb -c "\copy title_basics(tconst, titleType,primaryTitle,originalTitle,isAdult,startYear,endYear,runtimeMinutes,genres) FROM 'title.basics.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER"

    echo -e "${Bold}Loading Title Ratings...${NC}"
    psql --host=postgres --username=postgres -d imdb -c "\copy title_ratings(tconst, averageRating, numVotes) FROM 'title.ratings.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER"

    echo -e "${Bold}Loading Title Crew...${NC}"
    psql --host=postgres --username=postgres -d imdb -c "\copy title_crew(tconst, directors, writers) FROM 'title.crew.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER"

    echo -e "${Bold}Loading Title Principals...${NC}"
    psql --host=postgres --username=postgres -d imdb -c "\copy title_principals(tconst, ordering, nconst, category, job, characters) FROM 'title.principals.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER"

    echo -e "${Bold}Creating Denormalized Schemas...${NC}"
    psql --host=postgres --username=postgres -d imdb -f ./denormalized-schema.sql

    echo -e "${Bold}Creating Minimal Schemas...${NC}"
    psql --host=postgres --username=postgres -d imdb --set MINIMAL_DATASET_SIZE=$MINIMAL_DATASET_SIZE -f ./minimal-schema.sql
fi

# Change this banner: http://patorjk.com/software/taag/#p=display&v=1&f=Standard&t=POSTGRES%0AREADY%0AFOR%20USE
cat << "EOF"
  ____   ___  ____ _____ ____ ____  _____ ____  
 |  _ \ / _ \/ ___|_   _/ ___|  _ \| ____/ ___| 
 | |_) | | | \___ \ | || |  _| |_) |  _| \___ \ 
 |  __/| |_| |___) || || |_| |  _ <| |___ ___) |
 |_|    \___/|____/ |_| \____|_| \_\_____|____/ 
  ____  _____    _    ______   __               
 |  _ \| ____|  / \  |  _ \ \ / /               
 | |_) |  _|   / _ \ | | | \ V /                
 |  _ <| |___ / ___ \| |_| || |                 
 |_| \_\_____/_/   \_\____/ |_|                 
  _____ ___  ____    _   _ ____  _____          
 |  ___/ _ \|  _ \  | | | / ___|| ____|         
 | |_ | | | | |_) | | | | \___ \|  _|           
 |  _|| |_| |  _ <  | |_| |___) | |___          
 |_|   \___/|_| \_\  \___/|____/|_____|         
                                                
EOF

echo -e "${Bold}Summary:${NC}"

echo -e "  ${Bold}Name basics:${NC}"
echo "  Input: $(wc -l name.basics.tsv | awk '{print $1}')"
echo "  Rows: $(psql --host=postgres --username=postgres -d imdb -c 'select count(*) from name_basics' | head -3 | tail -1)"

echo -e "  ${Bold}Title basics:${NC}"
echo "  Input: $(wc -l title.basics.tsv | awk '{print $1}')"
echo "  Rows: $(psql --host=postgres --username=postgres -d imdb -c 'select count(*) from title_basics' | head -3 | tail -1)"

echo -e "  ${Bold}Title ratings:${NC}"
echo "  Input: $(wc -l title.ratings.tsv | awk '{print $1}')"
echo "  Rows: $(psql --host=postgres --username=postgres -d imdb -c 'select count(*) from title_ratings' | head -3 | tail -1)"

echo -e "  ${Bold}Title crew:${NC}"
echo "  Input: $(wc -l title.crew.tsv | awk '{print $1}')"
echo "  Rows: $(psql --host=postgres --username=postgres -d imdb -c 'select count(*) from title_crew' | head -3 | tail -1)"

echo -e "  ${Bold}Title principals:${NC}"
echo "  Input: $(wc -l title.principals.tsv | awk '{print $1}')"
echo "  Rows: $(psql --host=postgres --username=postgres -d imdb -c 'select count(*) from title_principals' | head -3 | tail -1)"
