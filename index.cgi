#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template, api.lib.SQL as SQL, api.lib.auth as auth
import os, Cookie, random

form = cgi.FieldStorage()

errors = []

template.output(head = '''<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
	<script src="jquery.event.drag-1.5.js" type="text/javascript"></script>
	<script src="jquery.event.drop-1.2.js" type="text/javascript"></script>
	<script src="jquery.cookie.js" type="text/javascript"></script>
	<script src="dictionary.js" type="text/javascript"></script>
	<script src="amalgam.js" type="text/javascript"></script>''', body = '''<div class="gamebox" id="gamebox"></div>
<div class="chat" id="chat"><div class="chatmsgs" id="chatmsgs"></div></div>
<p class="notes" id="players"></p>
<p class="notes"><a href="tos.cgi">Terms of Service</a></p>
<p class="notes"><a href="http://github.com/Kurper/amalgam">Source</a></p>
<form>
	<input type="hidden" id="sesskey" name="sesskey" value="0" />
	<input type="hidden" id="roomid" name="roomid" value="1" />
</form>''')
