import psycopg2, sys, traceback, StringIO, glob,tarfile, os
import databaseConfig
'''
Utility methods shared across various individual action scripts
'''

def connect():
    conn = psycopg2.connect(**databaseConfig.dbParams)
    cursor = conn.cursor()
    return conn, cursor

def dataPath(filename=None):
    path = databaseConfig.dataDir
    if(filename != None):
        path = os.path.join(path,filename)
    return path

def path(fileName):
    return os.path.join(mainDir+'/'+fileName)

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

