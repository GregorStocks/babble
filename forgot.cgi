#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template

template.output(title = "Forgot Password", body =
	'	<div class="userbox"><p>TOO BAD</p></div>')