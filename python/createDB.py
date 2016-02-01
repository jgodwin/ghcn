import psycopg2, sys, traceback, StringIO, glob,tarfile, os
from dbutil import *

'''
Automatically build the database and tables for the GHCN dataset
'''

def buildStationTable(cursor):
    execute(cursor,
    '''CREATE TABLE Station(
        SID CHAR(11) PRIMARY KEY,
        LAT REAL NOT NULL,
        LONG REAL NOT NULL,
        ELEVATION REAL NOT NULL,
        STATE CHAR(2), 
        NAME TEXT,
        GSN_FLAG CHAR(3), 
        HCN_CRN_FLAG CHAR(3),
        WMO_ID CHAR(5))''')

def buildStateTable(cursor):
    execute(cursor,'''CREATE TABLE State(
        CODE CHAR(2) PRIMARY KEY,
        NAME TEXT NOT NULL)''')

def buildCountryTable(cursor):
    execute(cursor,'''CREATE TABLE Country(
        CODE CHAR(2) PRIMARY KEY,
        NAME TEXT NOT NULL)''')

def buildInventoryTable(cursor):
    execute(cursor,'''CREATE TABLE Inventory(
        SID CHAR(11) NOT NULL references Station(SID) ON DELETE CASCADE,
        LAT REAL NOT NULL,
        LONG REAL NOT NULL,
        ELEMENT CHAR(4) NOT NULL,
        FIRSTYEAR INT,
        LASTYEAR INT)''')

# Add the column headers for each day of the month, there are 31 days max
def dailyColumnHeaders():
    text = ' '.join(['''
        VALUE%i INT, 
        MFLAG%i CHAR(1), 
        QFLAG%i CHAR(1), 
        SFLAG%i CHAR(1),''' % (i,i,i,i) for i in range(1,32)])
    return text.rstrip(',')

def buildObservationTable(cursor):
    # Should not pass variables around as strings,
    # but OK in this case because it is a one off, and we are not
    # passing user defined variables, only using as shortcut
    # for column name generation
    execute(cursor,'''CREATE TABLE Observation(
        SID CHAR(11) NOT NULL references Station(SID) ON DELETE CASCADE,
        YEAR INT NOT NULL,
        MONTH INT NOT NULL,
        ELEMENT CHAR(4) NOT NULL,
        %s)
        ''' % dailyColumnHeaders())
def buildUnitTable(cursor):
    execute(cursor,'''CREATE TABLE Unit(
        ELEMENT CHAR(4) PRIMARY KEY,
        CONVERSION FLOAT NOT NULL,
        SIUNIT VARCHAR NOT NULL,
        DESCRIPTION VARCHAR NOT NULL)''')

def buildStationCountryTable(cursor):
    execute(cursor,'''CREATE TABLE StationToCountry(
        SID CHAR(11) NOT NULL references Station(SID) ON DELETE CASCADE,
        CODE CHAR(2) NOT NULL references Country(CODE) ON DELETE CASCADE)
        ''')

def addPostGIS(cursor):
    execute(cursor,'''CREATE EXTENSION postgis;''')

def buildTables(cursor):
    buildStationTable(cursor)
    buildStateTable(cursor)
    buildObservationTable(cursor)
    buildCountryTable(cursor)
    buildInventoryTable(cursor)
    buildUnitTable(cursor)
    buildStationCountryTable(cursor)

def main():
  conn,cursor = connect()
  try:
      buildTables(cursor)
      conn.commit()
      try:
        addPostGIS(cursor)
        conn.commit()
      except Exception, e:
        # Postgis might already be installed, if so skip
        print e
  except Exception, e:
      traceback.print_exc()
      print 'error', e
  finally:
      cursor.close()
      conn.close()

main()
