#!/usr/bin/env python

import cgitb, cgi
cgitb.enable()

print "Content-type: text/plain;charset=utf-8"
# theoretically this should be served as application/json, but firefox opens up
# a "save" box for that instead of displaying it inline, which is inconvenient
# for debugging. 
print

form = cgi.FieldStorage()
if(form.has_key("eventnum")):
	eventnum = form["eventnum"].value
	# TODO: return all events since that one.

# backslashes etc need to be escaped for javascript, even though this is a raw string.
print r"""
[
	{
		type: 'words',
		words: 
			[
			'I', 'you', 'he', 'she', 'they', 'it', 'we',
			'is', 'are', 'eat', 'kill', 'cuss',
			'butt', 'human', 'pair',
			'groggy', 'ugly', 'dumb', 'fat', 'phat',
			'-s', '-y', '-ly', '-ize', '-ify', '-ed',
			'pro-', 'anti-',
			'nine', 'four', 'six', 'eighty',
			'ain\'t',
			'myself', 'yourself', 'herself',
			'my', 'your', 'her', 'his',
			'other',
			'who',
			'through', 'on', 'top', 'of', 'with', 'for', 'to', 'from', 'until',
			'the', 'even', 'so', 'well',
			'still', 'yet', 'also', 'and', 'too', 'but', 'while',
			'a', 'a', 'the', 'any', 'many',
			'!', '!', '.', '.', '?',
			',', ';', ':'
			]
	}
]
"""
