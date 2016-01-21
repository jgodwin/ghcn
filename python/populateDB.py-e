import psycopg2, sys, traceback, StringIO, glob,tarfile, os
from dbutil import *

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
    path = '/home/jeff/Dev/ghcn/data'
    tar = tarfile.open(path+gzFile,'r')
    i = 0
    for tarinfo in tar:
        name = tarinfo.name
        if tarinfo.isfile() and name.endswith('.dly'):
          f = tar.extractfile(tarinfo)
          print 'loading: %s ---- %d' %(name,i)
          loadObservation(name,f,cursor)
          i+=1
          if i > 1000:
            break
    tar.close()

def addLocationColumn(cursor):
    execute(cursor,
      '''Update Station SET location = ST_SetSRID(ST_MakePoint(long,lat),4326);''')

def createIndexes(cursor):
    execute(cursor,
      '''

def loadData(cursor):
    loadStations(cursor)
    loadStates(cursor)
    loadCountries(cursor)
    loadInventory(cursor)
    loadObservations(cursor)
    
def main():
   conn,cursor = connect() 
   try:
       loadData(cursor)
       addLocationColumn(cursor)
       conn.commit()
   except:
      traceback.print_exc()
      print 'error', e
   finally:
       cursor.close()
       conn.close()
