import os
# Put in your database parameters here

# Name of the new DB that will be used
db = 'ghcn'

# Login info/credentials for your postgres installation
dbParams = {
    'database':db,
    'user':os.environ.get('POSTGRES_USER'),
    'password':os.environ.get('POSTGRES_PW'),
    'host':'localhost',
    'port':5432
}

########### More config params #############

# Google Geocoding API Key
geoAPIKey = os.environ.get('GEOCODE_API_KEY')

# Data directory, where downloaded files will go
dataDir='data'

# If not None, will ONLY load the first N files from the data tarfile,
# setting this number to a low value will avoid loading the entire database,
# but still give you enough data to play with: recommended value - 1000
subsetData=None
