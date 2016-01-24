from ftplib import FTP
import sys, dbutil, os

FTP_URL='ftp.ncdc.noaa.gov'
FTP_DIR='/pub/data/ghcn/daily/'

files = [
        'ghcnd-inventory.txt',
        'ghcnd-stations.txt',
        'ghcnd-countries.txt',
        'ghcnd-states.txt',
        'ghcnd-version.txt',
        'ghcnd_all.tar.gz'
        ]

ftp = FTP(FTP_URL)

ftp.login()
ftp.cwd(FTP_DIR)

# Make the directory if it does not exist
os.mkdir(dbutil.dataPath())

for file in files:
    try:
        print 'Retrieving... '+file
        fout = open(dbutil.dataPath(file),'w')
# switch to binary mode
        ftp.sendcmd("TYPE i")
        size = ftp.size(file)
# switch back to ascii mode
        ftp.sendcmd("TYPE A")
        sum = 0
        def wstatus(data):
            global sum
            fout.write(data)
            sum += len(data)
            sys.stderr.write('\rREAD: '+str(float(sum)/size*100))
        ftp.retrbinary('RETR '+file,wstatus)
        fout.flush()
        fout.close()
        sys.stderr.write('\r')
    except KeyboardInterrupt:
        print 'User terminated'
        raise
    except Exception, e:
        print 'Error', e

ftp.quit()


