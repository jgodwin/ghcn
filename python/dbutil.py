import sys, traceback, StringIO, glob,tarfile, os, databaseConfig
try:
    import psycopg2
except:
    traceback.print_exc()
    print '\n\n******\nYou must download PsycoPG2, or your PsycoPG2 is out-of-date'
    sys.exit(1)

'''
Utility methods shared across various individual action scripts
'''

log=True

def connect():
    if databaseConfig.dbParams['user'] == None:
        databaseConfig.dbParams['user'] = raw_input('Enter username: ')
    if databaseConfig.dbParams['password'] == None:
        databaseConfig.dbParams['password'] = raw_input('Enter password: ')
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


