import psycopg2, sys, traceback, StringIO, glob,tarfile

mainDir = '/home/jeff/Dev/ghcn_data'
def path(fileName):
    return mainDir+'/'+fileName

countries = path('ghcnd-countries.txt')
states = path('ghcnd-states.txt')
inventory=path('ghcnd-inventory.txt')
stations=path('ghcnd-stations.txt')
observationDir = '/home/jeff/Dev/ghcn_data/ghcnd_all'
log = True

def text(nIndent, message):
    if log:
        print ''.join(['-' for i in range(nIndent)])+'>' + message

def execute(cursor, sql, *varargs):
    text(10,sql)
    if not sql.endswith(';'):
        sql += ';'
    cursor.execute(sql, varargs)

def clearTables(cursor):
    result = raw_input('ARE YOU SURE YOU WANT TO DROP ALL TABLES?')
    if result == 'y':
        text(2,'DROPPING TABLES')
        for table in ['Station', 'Observation', 'State', 'Country', 'Inventory']:
            execute(cursor,'''DROP TABLE %s CASCADE;''' % table)
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
        SID CHAR(11) NOT NULL references Station(SID),
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
        SID CHAR(11) NOT NULL references Station(SID),
        YEAR INT NOT NULL,
        MONTH INT NOT NULL,
        ELEMENT CHAR(4) NOT NULL,
        %s)
        ''' % dailyColumnHeaders())

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

def loadAndSplitFile(fileName,cols,table,cursor,fileLike=None):
    delim = '\t'
    f = StringIO.StringIO()
    print 'LOADING FILE: ' + fileName
    fr = open(fileName,'r') if fileLike==None else fileLike
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
    gzFile = '/ghcnd_all.tar.gz'
    path = '/home/jeff/Dev/ghcn_data'
    outputPath=path+'/ghcnd_all'
    tar = tarfile.open(path+gzFile,'r')
    i = 0
    for tarinfo in tar:
        name = tarinfo.name
        if tarinfo.isfile() and name.endswith('.dly'):
          f = tar.extractfile(tarinfo)
          print 'loading: %s ---- %d' %(name,i)
          loadObservation(name,f,cursor)
          i+=1
    tar.close()
        
def buildTables(cursor):
    buildStationTable(cursor)
    buildStateTable(cursor)
    buildObservationTable(cursor)
    buildCountryTable(cursor)
    buildInventoryTable(cursor)

def loadData(cursor):
    loadStations(cursor)
    loadStates(cursor)
    loadCountries(cursor)
    loadInventory(cursor)
    loadObservations(cursor)

def main():
  conn = psycopg2.connect(database='ghcn',
      user='postgres',
      password='postgres',host='localhost')

  cursor = conn.cursor()
  try:
      clearTables(cursor)
      buildTables(cursor)
      loadData(cursor)
      # Need to add manual commands to create PostGIS database:
      # CREATE EXTENSION postgis;
      # ALTER TABLE Station ADD COLUMN location geography(Point,4326);
      # Update Station SET location = ST_SetSRID(ST_MakePoint(long,lat),4326);

      conn.commit()
  except Exception, e:
      traceback.print_exc()
      print 'error', e
  finally:
      cursor.close()
      conn.close()

main()
