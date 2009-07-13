#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template, api.lib.SQL as SQL, api.lib.auth as auth
import os, Cookie, random

form = cgi.FieldStorage()

username = None
sesskey = None
errors = []

# logging in from the form
if(form.has_key('username') and form.has_key('password')):
	conn = SQL.get_conn()
	cursor = SQL.get_cursor(conn)
	cursor.execute('SELECT id, passhash FROM users WHERE username = %s', form.getfirst('username'))
	row = cursor.fetchone()
	if row:
		passhash = row['passhash']
		if auth.hashes_to(form.getfirst('password'), passhash):
			# new session key
			username = form.getfirst('username')
			sesskey = hex(random.randint(0, 16 ** 10))
			cursor.execute('UPDATE users SET sesskey = %s WHERE id = %s', (sesskey, row['id']))
	if not username:
		errors.append('<p>Invalid username/password combination!</p>')

# check cookie
if not username:
	pass

# not logged in
if not username:
	template.output(body = '''	<div class="userbox">%s
		<form action="index.cgi" method="post">
			<p>Username: <input type="text" name="username" /></p>
			<p>Password: <input type="password" name="password" /></p>
			<input type="submit" value="Log In" name="submit" />
			<p class="notes"><a href="register.cgi">Register</a></p>
			<p class="notes"><a href="forgot.cgi">Forgot your password?</a></p>
		</form>
	</div>''' % '\n'.join(errors))
else:
	template.output(head = '''<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
	<script src="jquery.event.drag-1.5.js" type="text/javascript"></script>
	<script src="jquery.event.drop-1.2.js" type="text/javascript"></script>
	<script src="dictionary.js" type="text/javascript"></script>
	<script src="amalgam.js" type="text/javascript"></script>''', body = '''<div class="wordsbox wordscontainer" id="wordsbox"></div>
<div class="dropbox wordscontainer" id="dropbox">
	<div class="prop" id="prop"></div>
	<div class="clear" id="clear"></div>
</div>
<p class="sentence" id="sentence"></p>
<p class="notes">You are logged in as "%s"</p>
<p class="notes">By the way, you probably shouldn't resize the browser window.</p>
<p class="notes"><a href="tos.cgi">Terms of Service</a></p>
<p class="notes"><a href="http://github.com/Kurper/amalgam">Source</a></p>
<form>
	<input type="hidden" id="username" name="username" value="%s" />
	<input type="hidden" id="sesskey" name="sesskey" value="%s" />
</form>''' % (username, username, sesskey))
