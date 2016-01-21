#Start here

Contains python scripts used to initialize the database, to get started you will need Python 2.7 and psycopg.

To start:
```
python downloadData.py
python createDB.py
python populateDB.py
```

If you want to use only a subset of the data (recommended for personal machines), then you can set a flag inside of populateDB.py to randomly choose so many records.

At some point if you want to update the DB with a fresh set of data, then you should download the latest GHCN data, and run:
```
python updateDB.py
```
