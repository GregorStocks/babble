#!/usr/bin/env python
from __ future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template, api.lib.SQL as SQL
import re

form = cgi.FieldStorage(keep_blank_values = "1")

if(form.has_key('username') and form.has_key('password')):
	username = form.getfirst('username')
	password = form.getfirst('password')
	email = form.getfirst('email', None)
	legal = form.getfirst('legal', False)
	errors = []
	if not re.match(r'^\w+$', username):
		errors.append('Usernames can only contain letters, numbers, and underscores')
	if len(username) > 20:
		errors.append('Your username is too long.')
	
	if not errors:
		conn = SQL.getConn()
		cursor = SQL.getCursor(conn)
		cursor.execute("SELECT * FROM users WHERE username = %s", username)
		if cursor.fetchall():
			errors.append('This username is already in use.') 

	if not legal:
		errors.append('You must be 18 to play Amalgam.')
	
	if errors:
		errorhtml = '<p>'
		for error in errors:
			errorhtml += error + '<br>'
		errorhtml += '</p>'
		template.output(title = 'Registration failed!',
		                    body = '	<div class="userbox">' + errorhtml + """
		<p><a href="register.cgi">Try Again</a></p></div>""")
	
	else:
		template.output()
		# add user to database

		# log them in
		pass
else:
	template.output(title = "Register", body = """	<div class="userbox">
	<form action="register.cgi" method="post">
		<p>Username: <input type="text" name="username" /></p>
		<p>Password: <input type="password" name="password" /></p>
		<p>Email (optional): <input type="text" name="email" /></p>
		<p>I'm at least 18: <input type="checkbox" name="legal" /></p> 
		<p><input type="submit" value="Register" name="submit" /></p>
	</form>
	</div>""")
