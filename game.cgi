#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import api.lib.template as template

template.output(head = """
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
	<script src="jquery.event.drag-1.5.js" type="text/javascript"></script>
	<script src="jquery.event.drop-1.2.js" type="text/javascript"></script>
	<script src="dictionary.js" type="text/javascript"></script>
	<script src="amalgam.js" type="text/javascript"></script>""", body = """<div class="wordsbox wordscontainer" id="wordsbox"></div>
<div class="dropbox wordscontainer" id="dropbox">
	<div class="prop" id="prop"></div>
	<div class="clear" id="clear"></div>
</div>
<p class="sentence" id="sentence"></p>
<p class="notes">By the way, you probably shouldn't resize the browser window.</p>
<p class="notes"><a href="tos.cgi">Terms of Service</a></p>
<p class="notes"><a href="http://github.com/Kurper/amalgam">Source</a></p>""")
