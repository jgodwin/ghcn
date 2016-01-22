from dbutil import *


def clearTables(cursor):
    print '******* WARNING THIS WILL DELETE ALL DATA FROM YOUR DB ******'
    result = raw_input('ARE YOU SURE YOU WANT TO DROP ALL TABLES?')
    if result == 'y':
        text(2,'DROPPING TABLES')
        for table in ['Station', 'Observation', 'State', 'Country', 'Inventory']:
            execute(cursor,'''DROP TABLE %s CASCADE;''' % table)

conn,cursor = connect()
try:
    clearTables(cursor)
    conn.commit()
finally:
    cursor.close()
    conn.close()
