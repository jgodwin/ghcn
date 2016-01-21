# Put in your database parameters here

# Name of the DB that your user can log into to create a new DB
# for default postgres installs, this should be 'postgres'
masterDB = 'postgres'

# Name of the new DB that will be created for this dataset
newDB    = 'ghcn'

# Login info/credentials for your postgres installation
dbParams = {
    user='jgodwin',
    password='harrison2',
    host='localhost',
    port=5432
}

########### More config params #############

# Data directory, where downloaded files will go
dataDir='data'

# If not None, will ONLY load the first N files from the data tarfile,
# setting this number to a low value will avoid loading the entire database,
# but still give you enough data to play with: recommended value - 1000
subsetData=1000
