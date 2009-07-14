from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import cjson as json

def output(title = "", head = "", body = ""):
	if title:
		title = '%s - Amalgam' % str(title)
	else:
		title = 'Amalgam'
	if head:
		head = '\n\t%s' % str(head)

	print "Content-type: text/html;charset=utf-8"
	print
	print """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>%s</title>
	<link rel="shortcut icon" href="/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="style.css" />
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />%s
</head>
<body>
%s
</body>
</html>""" % (str(title), str(head), str(body))

def debug(text):
	print "Content-type: text/html"
	print
	print "DEBUG: %s" % str(text)

def output_json(obj):
	# theoretically this should be served as application/json, but firefox opens up
	# a "save" box for that instead of displaying it inline, which is inconvenient
	# for debugging. 
	print "Content-type: text/plain;charset=utf-8"
	print
	print json.dumps(obj, indent=4)
