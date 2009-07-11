#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template

template.output(body = """	<div class="userbox">
		<form action="game.cgi" method="post">
			<p>Username: <input type="text" id="username" /></p>
			<p>Password: <input type="password" id="password" /></p>
			<input type="submit" value="Log In" id="submit" />
			<p class="notes"><a href="register.cgi">Register</a></p>
			<p class="notes"><a href="forgot.cgi">Forgot your password?</a></p>
		</form>
	</div>""")
