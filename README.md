# A Data Visualization Web App for the Global Historical Climatological Network Data
The purpose of this project is to allow users to quickly peruse the GHCN dataset, which is a veritable treasure trove of climatological data spanning hundreds of years for thousands of stations around the world. Full details are available [here](https://www.ncdc.noaa.gov/data-access/land-based-station-data/land-based-datasets/global-historical-climatology-network-ghcn).
NOAA has a [viewer](http://www.ncdc.noaa.gov/cdo-web/results) for this dataset, but it is quite limited in its utility and seemingly provides access only to the data in bulk.  [NASA has another viewer here](http://data.giss.nasa.gov/gistemp/station_data/) for a similar dataset.  NOAA has other plots that show summary statistics gleaned from this dataset, but as far as I can tell there is no way to view the underlying data directly from the web.  Instead, you have to download the files from FTP, rebuild the database yourself, and then put together a front-end to view it.  So this project is an attempt at doing just that, but with the hope that other people might find some utility in it as well.

Thus, the goals of this project are three-fold: 

1. Provide an easily accessible web-based data visualization tool that lets users quickly get a synopsis of available data for particular geographic areas at particular times; 
2. Create a RESTful interface that could be used to directly provide access to the desired data; 
3. Ensure that this web app is reproducible, and scripted such that it can be easily kept in sync with the master NOAA database.


