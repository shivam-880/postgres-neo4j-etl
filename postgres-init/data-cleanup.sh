#!/usr/bin/env bash

export JAVA_OPTS="-Xmx8g"

Bold='\033[1m'   # Bold
NC='\033[0m'     # No Color

if ! command -v wget &> /dev/null
then
    echo "This script requires 'wget' to work"
    exit 1
fi

CREW_LIMIT=5
FILES=('name.basics.tsv' 'title.basics.tsv' 'title.principals.tsv' 'title.ratings.tsv' 'title.crew.tsv')

# Move to scripts folder
cd "$(dirname "$0")"

echo -e "${Bold}Removing all but movies from titles...${NC}"
grep "movie" title.basics.tsv > title.basics.only_movies && \
    head -1 title.basics.tsv > title.basics.head && \
    cat title.basics.head title.basics.only_movies > title.basics.tsv && \
    rm title.basics.only_movies title.basics.head

# We need to replace double quotes to avoid issues during loading data using 'COPY' command.
echo -e "${Bold}Replacing double quotes...${NC}"
for file in "${FILES[@]}"; do
    sed -i "s/\"/'/g" "$file"
done

# There are titles and names that don't appear in 'title.principals.tsv'.
# We need to rid missing values to avoid issues at import.
echo -e "${Bold}Checking Foreign Key Constraints...${NC} (this will take some time)"
awk '{print $1}' name.basics.tsv > only_names.fk
awk '{print $1}' title.basics.tsv > only_titles.fk

echo -e "  ${Bold}Removing missing principals...${NC}"
cs launch ammonite --scala 2.12.10 -- rid-missing.sc ridMissingPrincipals && mv title.principals.cleaned title.principals.tsv

echo -e "  ${Bold}Removing missing ratings...${NC}"
cs launch ammonite --scala 2.12.10 -- rid-missing.sc ridMissingRatings && mv title.ratings.cleaned title.ratings.tsv

# There are 'directors' or 'writers' that exceed 3000 characters,
# truncate both to a limit of 'CREW_LIMIT' for the sake of brevity.
echo -e "${Bold}Truncating Crew size...${NC}"
nconst_length=$(tail -1 only_names.fk | awk '{ print length($0) }')
let "nconst_limit=((${nconst_length} + 1) * $CREW_LIMIT) - 1"
awk -F $'\t' -v limit="$nconst_limit" 'BEGIN {OFS = FS} { $2=substr($2,1,limit); $3=substr($3,1,limit); print }' title.crew.tsv > title.crew.tmp && \
mv title.crew.tmp title.crew.tsv

echo -e "  ${Bold}Removing missing crew...${NC}"
cs launch ammonite --scala 2.12.10 -- rid-missing.sc ridMissingCrew && mv title.crew.cleaned title.crew.tsv

echo -e "  ${Bold}Removing unnecessary names...${NC}"
awk '{print $3}' title.principals.tsv | sort | uniq > only_principals.fk
cs launch ammonite --scala 2.12.10 -- rid-missing.sc ridUnnecessaryNames && mv name.basics.cleaned name.basics.tsv

echo -e "${Bold}Cleaning workspace...${NC}"
rm *.fk 
