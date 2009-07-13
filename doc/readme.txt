things to do:

1. set up a database

2. modify the connection settings in api/lib/const/config.py to work properly 
with your database

3. ensure that directories that shouldn't be publicly accessible (currently doc/ 
and api/lib/) aren't, using .htaccess or your server's equivalent.

4. ensure that every .cgi file in / and api/ is executable (chmod +x api/*.cgi 
*.cgi)
