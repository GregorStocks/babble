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
	<script src="jquery.rightClick.js" type="text/javascript"></script>
	<script src="jquery.disable.text.select.js" type="text/javascript"></script>
	<script src="sha1.js" type="text/javascript"></script>
	<script src="dictionary.js" type="text/javascript"></script>
	<script src="amalgam.js" type="text/javascript"></script>''', body = '''<div class="gamebox" id="gamebox"></div>
<<<<<<< HEAD:index.cgi
<div id="timer"><div id="timediv"><p id="time" /></div><div id="timerfull"></div></div>
<br />
<div class="chat" id="chat">
	<div class="members" id="members"><table id="membertable"><tr><th>Name</th><th>Score</th></tr></table></div>
	<div class="chatmsgs" id="chatmsgs"></div><div class="clear"></div>
</div>
<div id="footer">
<p class="notes"><a href="tos.cgi">Terms of Service</a></p>
<p class="notes"><a href="http://github.com/Kurper/amalgam">Source</a></p>
<p class="notes">If you're using Firefox, be sure that Javascript is allowed to disable context menus for the most pleasant user experience when resetting "==". If you're using Opera 9.5+, you apparently need to enable some setting somewhere to let this detect right-clicks so you can reset "==" at all, and can't disable context menus no matter what</p>
</div>
<form action="" name="hiddenthings">
	<input type="hidden" id="sesskey" name="sesskey" value="0" />
	<input type="hidden" id="roomid" name="roomid" value="1" />
</form>''')
