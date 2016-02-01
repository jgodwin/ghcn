import psycopg2, sys, traceback, StringIO, glob,tarfile, os
from dbutil import *
import databaseConfig
import units

gzFile = 'ghcnd_all.tar.gz'
states = 'ghcnd-states.txt'
inventory='ghcnd-inventory.txt'
countries='ghcnd-countries.txt'
stations ='ghcnd-stations.txt'

# delimiter to use for postgres bulkload
delim = '\t'
# Given a line of text with fixed width cols in it, and a list of columns, 
# that correspond to the indices in the text to extract, extract each column
# and return the values in a list
def splitFixedWidthCols(line, cols=[]):
    split = []
    for col in cols:
        #assume 1-based indices, and end is inclusive 
        start = col[0]-1
        end = len(line) if col[1] < 0 else col[1]
        val = line[start:end]
        val = val.rstrip()
        split.append(val)
    assert len(split) == len(cols)
    return split

# Read all the lines from the file, in this case a tar file in memory,
# and then convert to a delimited format as the files are originally 
# in fixed column format, which cannot use postgres bulk load function
def loadAndSplitFile(fileName,cols,table,cursor,fileLike=None):
    f = StringIO.StringIO()
    print 'LOADING FILE: ' + fileName
    fr = open(dataPath(fileName),'r') if fileLike==None else fileLike
    lines = fr.readlines()
    fr.close()
    for line in lines:
        split = delim.join(splitFixedWidthCols(line, cols))
        if not split.endswith('\n'):
            split+= '\n'
        f.write(split)
    f.flush()
    f.seek(0)
    cursor.copy_from(f,table,sep=delim)
    f.close()
   
def loadUnits(cursor):
    f = StringIO.StringIO()
    units.writeUnits(f,delim)
    f.flush()
    f.seek(0)
    cursor.copy_from(f,'Unit',sep=delim)
    f.close()

def loadStates(cursor):
    loadAndSplitFile(states,
        [(1,2), (4,-1)],
        'State',cursor)

def loadCountries(cursor):
    loadAndSplitFile(countries,
        [(1,2), (4,-1)],
        'Country',cursor)

def loadInventory(cursor):
    loadAndSplitFile(inventory,
        [(1,11), (13,20),(22,30),(32,35),(37,40),(42,45)],
        'Inventory',cursor)

def loadStations(cursor):
    loadAndSplitFile(stations,
        [(1,11), (13,20),(22,30),(32,37),(39,40),(42,71),
         (73,75),(77,79),(81,85)],
        'Station',cursor)

def monthCols(start):
    cols = []
    for i in range(31):
        cols.append((start,start+4))
        start += 5
        for i in range(3):
            cols.append((start,start))
            start+=1
    return cols

def loadObservation(name,fileLike,cursor):
    loadAndSplitFile(name,
    [(1,11),(12,15),(16,17),(18,21)] + 
    monthCols(22), 'Observation',cursor,fileLike)

def loadObservations(cursor):
    path = dataPath(gzFile)
    tar = tarfile.open(path,'r')
    i = 0
    subset=databaseConfig.subsetData != None
    maxN = databaseConfig.subsetData if subset else -1
    for tarinfo in tar:
        name = tarinfo.name
        if tarinfo.isfile() and name.endswith('.dly'):
          f = tar.extractfile(tarinfo)
          print '\rloading: %s ---- %d/%d' %(name,i,maxN)
          loadObservation(name,f,cursor)
          i+=1
          if subset and i > databaseConfig.subsetData:
            break
    tar.close()

def setLocationColumn(cursor):
    execute(cursor,
      '''Update Station SET location = ST_SetSRID(ST_MakePoint(long,lat),4326);''')

def createIndexes(cursor):
    # Index inventories for faster queries on sid and/or element
    execute(cursor,
      '''
      CREATE INDEX idx_inventory_primary on Inventory (sid,element,firstyear,lastyear);
      ''')
    # Stations already has an index on sid as it is a primary key
    # Make an index for locations for spatial queries
    execute(cursor,
      '''
      CREATE INDEX idx_station_location on Station(location);
      ''')
    # Index observations on sid and element and year and month
    # This is the very long running indexing operation, which should
    # only be done once, makes most observation queries very fast
    print 'Starting observation index, WARNING THIS CAN TAKE A LONG TIME'
    execute(cursor,
      '''
      CREATE INDEX idx_observation_primary on Observation(sid,element,year,month);
      ''')

def addLocationColumn(cursor):
    # 4326 represents WGS84
    execute(cursor,
            '''ALTER TABLE Station ADD COLUMN location geometry(Point,4326);''')

def loadData(cursor):
    #TODO: wrap each in separate transaction
    loadStations(cursor)
    loadStates(cursor)
    loadCountries(cursor)
    loadInventory(cursor)
    loadObservations(cursor)
    loadUnits(cursor)
    
def main():
   conn,cursor = connect() 
   try:
       print 'Loading data...'
       loadData(cursor)
       print 'Adding location column...'
       addLocationColumn(cursor)
       setLocationColumn(cursor)
       conn.commit()
       print 'Starting index generation...'
       createIndexes(cursor)
       conn.commit()
   except Exception, e:
      traceback.print_exc()
      print 'error', e
   finally:
       cursor.close()
       conn.close()

main()
