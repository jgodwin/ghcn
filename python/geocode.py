# Geocode the stations if possible
import databaseConfig
import urllib2, json

googleAPIURL = 'https://maps.googleapis.com/maps/api/geocode/json?'

def createURL(lat,lng):
    return googleAPIURL+'latlng=%s,%s&key=%s' % (lat,lng,
            databaseConfig.geoAPIKey)

def geocodeStations(stationFileName):
    if databaseConfig.geoAPIKey != None:
        f = open(stationFileName,'r')
        lines = f.readlines()
        for line in lines:
            sid, lat, lng = line[0:11], line[12:20], line[21:30]
            url = createURL(lat.strip(),lng.strip())
            result = urllib2.urlopen(url).read()
            parsed = json.loads(result)
            found = False
            if parsed != None:
                try:
                    results = parsed['results']
                    for result in results:
                        if (found):
                            break
                        if result.has_key('address_components'):
                            components = result['address_components']
                            for component in components:
                                if (found):
                                    break
                                if 'country' in component['types']:
                                    code = component['short_name']
                                    found = True
                                    print sid, lat, lng, code, component['long_name']
                except KeyError:
                    found = False
            if not found:
                print 'COULD NOT GEOCODE: ',sid
        f.close()

geocodeStations('data/ghcnd-stations.txt')
