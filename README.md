# Showroute

Visualise `KML` route file on Google Maps. You can export KML files from [Google Maps timeline](https://www.google.com/maps/timeline?pb). Showroute can only map Finnish location category names (such as *Driving* and *Restaurant*).

![Example visualisation](showroute.gif)

## Prerequisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.0.0 or above installed.

Set `GOOGLE_MAPS_API_KEY` environment variable with your [Google Maps API key](https://developers.google.com/maps/documentation/javascript/get-api-key).

## Running

- Place your KML file in `resources/route.kml`.
- Start the web server for the application: `lein ring server`

## Todo

- Upload and list KML files
- Combine a set of KML files
- Show timestamps between KML `Placemark`s.
- Figure out localized category names for other languages and map them to same icons.
- Select best possible zoom level based on the coordinates.

## License

Showroute's source code is licensed with the MIT License, see [LICENSE.txt](LICENSE.txt).

## Thanks

This project is a grateful recipient of the [Futurice Open Source sponsorship program](http://futurice.com/blog/sponsoring-free-time-open-source-activities?utm_source=github&utm_medium=spice) 🌶🦄.
