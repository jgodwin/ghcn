#Start here

Contains python scripts used to initialize the database, to get started you will need:
- Python 2.7
- psycopg2
- PostgreSQL >9.4
- PostGIS > 2.0

I do not provide instructions on how to install Postgres as it can be quite complicated or easy depending on your OS and local development environment. On Mac it is simplest to use Homebrew to install those packages.

In any case, once you get those packages installed, you should go ahead and start the Postgres server and create a database:
```
psql
CREATE DATABASE ghcn;
```

Once the database is created, edit databaseConfig.py to reflect your postgres install.

Then, you need to execute the following three commands:
```
python downloadData.py
python createDB.py
python populateDB.py
```
If something went wrong with the generation of the database then you can use:
```
python cleanDB.py
```
Which will DROP all the tables and data, allowing you to restart over fresh.

At some point if you want to update the DB with a fresh set of data, then you should download the latest GHCN data, and run:
```
python updateDB.py
```

