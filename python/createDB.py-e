import psycopg2, sys, traceback, StringIO, glob,tarfile, os
from dbutil import *

mainDir = '/home/jeff/Dev/ghcn/data'
countries = path('ghcnd-countries.txt')
states = path('ghcnd-states.txt')
inventory=path('ghcnd-inventory.txt')
stations=path('ghcnd-stations.txt')
observationDir = '/home/jeff/Dev/ghcn/data'
log = True

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

def addPostGIS(cursor):
    execute(cursor,'''CREATE EXTENSION postgis;''')

def addGeomColumn(cursor):
    # 4326 represents WGS84
    execute(cursor,
            '''ALTER TABLE Station ADD COLUMN location geography(Point,4326);''')

def buildTables(cursor):
    buildStationTable(cursor)
    buildStateTable(cursor)
    buildObservationTable(cursor)
    buildCountryTable(cursor)
    buildInventoryTable(cursor)

def main():
  conn,cursor = connect()
  try:
      buildTables(cursor)
      conn.commit()
  except Exception, e:
      traceback.print_exc()
      print 'error', e
  finally:
      cursor.close()
      conn.close()

main()
