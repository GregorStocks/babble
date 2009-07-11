#!/usr/bin/env python
from __ future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template

template.output(title = "Terms of Service", body = """<h2>Terms of Service</h2>
<ol>
	<li>You're not going to read these.</li>
	<li>They wouldn't stand up in court anyways.</li>
	<li>I can do whatever I want and you can't stop me.</li>
</ol>""")
