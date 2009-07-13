#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template, api.lib.SQL as SQL, api.lib.auth as auth
import re

form = cgi.FieldStorage()

if(form.has_key('username') and form.has_key('password')):
	username = form.getfirst('username', '').strip()
	password = form.getfirst('password')
	email = form.getfirst('email', None)
	legal = form.getfirst('legal', False)
	errors = []
	if not re.match(r'^\w+$', username):
		errors.append('Usernames can only contain letters, numbers, and underscores.')
	if len(username) > 20:
		errors.append('Your username is too long.')
	if not username:
		errors.append('Your username must contain at least one character.')
	
	conn = SQL.get_conn()
	cursor = SQL.get_cursor(conn)
	if not errors:
		cursor.execute("SELECT * FROM users WHERE username = %s", username)
		if cursor.fetchall():
			errors.append('This username is already in use.') 

	if not legal:
		errors.append('You must be 18 to play Amalgam.')
	
	if errors:
		template.output(title = 'Registration failed',
		                    body = """	<div class="userbox">
		%s
		<p><a href="register.cgi">Try Again</a></p></div>""" % '\n\t\t'.join(['<p>%s</p>' % error for error in errors]))
	
	else:
		# add user to database
		passhash = auth.hash_pass(password)
		if not email:
			email = None # 
		cursor.execute('INSERT INTO users (username, passhash, email) VALUES (%s, %s, %s)', (username, passhash, email)) 

		# TODO: automatically log them in
		template.output(title = "Registered", body =
			'''	<div class="userbox"><p>Account %s created!</p>
			<p><a href="index.cgi">Return to main page</a></p></div>''' % username)

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
