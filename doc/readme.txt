things to do:

1. set up a database
2. modify the connection settings (currently in api/eventssince.cgi, soon to be somewhere more convenient) to work properly with your database
3. ensure that directories that shouldn't be publicly accessible (currently uh none) aren't, using .htaccess or your server's equivalent
4. ensure that every .cgi file in api/ is executable (chmod +x api/*.cgi)
