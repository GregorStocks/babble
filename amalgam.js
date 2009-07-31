function add_suffix(phrase, suffix) {
	// strip the leading dash
	var suff = suffix.replace(/^-(.*)$/, '$1');
	if(suff == 's') { // pluralization, hoo boy
		// don't try to implement a fully correct pluralization algorithm, since our set of words is small
		// (relative to the english language anyways)
		// using a cut-down form of the algorithm described in Conway's "An Algorithmic Approach to English
		// Pluralization"
		// special dictionary-defined cases are handled before we ever get to this function
		if(phrase.match(/ch$/) || phrase.match(/sh$/) || phrase.match(/ss$/) || phrase.match(/x$/) || phrase.match(/is$/)) {
			suff = 'es';
		} else if(phrase.match(/[aeo]lf$/) || phrase.match(/[^d]eaf$/) || phrase.match(/arf$/)) {
			return phrase.replace(/f$/, 'ves');
		} else if(phrase.match(/[nlw]ife$/)) {
			return phrase.replace(/fe$/, 'ves');
		} else if(phrase.match(/y$/) && !(phrase.match(/[aeiou]y$/) || phrase.match(/[A-Z].*y$/))) {
			return phrase.replace(/y$/, 'ies');
		} else if(phrase.match(/o$/) && !phrase.match(/[aeiou]o$/)) {
			suff = 'es';
		}
	} else if(suff == 'y') {
		if(phrase.match(/t$/)) {
			suff = 'ty';
		}
	} else if(suff == 'ly') {
		if(phrase.match(/ic$/)) {
			suff = 'ally';
		} else if(phrase.match(/le$/)) {
			return phrase.replace(/e$/, "y");
		}
	} else if(suff == "ed") {
		if(phrase.match(/y$/)) {
			return phrase.replace(/y$/, 'ied');
		} else if(phrase.match(/e$/)) {
			suff = "d";
		} else if(phrase.match(/lf$/)) {
			return phrase.replace(/lf$/, 'ved');
		}
	} else if(suff == "ing") {
		if(phrase.match(/e$/)) {
			return phrase.replace(/e$/, "ing");
		}
	} else if(suff == "ly") {
		if(phrase.match(/ic$/)) {
			suff = "ally";
		}
	}
	return phrase + suff;
}

function add_prefix(phrase, prefix) {
	var pref = prefix.replace(/^(.*)-$/, "$1");
	return pref + phrase;
}

function capitalize(word) {
	if(word == "!") {
		return "I";
	 }else {
		return word.substring(0, 1).toUpperCase() + word.substring(1, word.length);
	}
}

function capitalize_display(word) {
	return word.substring(0, 1).toUpperCase() + word.substring(1, word.length);
}

function add_to_sentence(sentence, phrase, sentence_start, prefixes) {
	// apply all prefixes
	while(prefixes.length) {
		phrase = add_prefix(phrase, prefixes.pop());
	}
	if(sentence_start) {
		phrase = capitalize(phrase);
	}

	// a -> an
	// this is a pretty ugly way to do this but OH WELL
	if(sentence.match(/ a$/i) || sentence.match(/^a$/i)) {
		if(phrase.match(/^[aeiou]$/i) || phrase.match(/^h[aeiou]/i)) {
			if(!phrase.match(/^uni/i)) {
				sentence += "n";
			}
		}
	}

	if(sentence.length > 0) {
		sentence += " " + phrase;
	} else {
		sentence = phrase;
	}
	return sentence
}

function updateSentence() {
	$('#sentence').empty();
	var words = []
	$.each($('#dropbox > .wordbox'), function(idx, box) {
		var idstr = $(box).attr('id');
		var i = idstr.match(/\d+/);
		if(i) {
			var idnum = parseInt(i);
			var word = wordlist[idnum];
			if(word == "==") {
				wordtext = $(box).text();
				i = 0;
				b = $(".wordbox:not(.copy):contains('" + wordtext + "')")
				if(b && b.attr('id')) {
					i = b.attr('id').match(/\d+/);
				}
				if(i) {
					idnum = parseInt(i);
					word = wordlist[idnum];
				} else {
					word = "==";
				}
			}
			words.push(word);
		}
	});
	$("#sentence").append(makeSentence(words));
	sendSentence(words);
}

function makeSentence(words) {
	var prefixstack = [];
	var sentence = "";
	var curphrase = "";
	var sentence_start = true;
	var capitalize_next = false;
	var phrase_done = false;
	var connecting = false;
	for(var wordnum in words) {
		var word = words[wordnum];
		var dict_entry = dictionary[word];
		if(dictionary[curphrase] && dictionary[curphrase]['combos'] && dictionary[curphrase]['combos'][word]) { // a special way to combine
			connecting = false;
			curphrase = dictionary[curphrase]['combos'][word];
		} else if(word == "^") {
			capitalize_next = true;
			phrase_done = true;
		} else if(word == "++") {
			connecting = true;
		} else if(dict_entry && dict_entry["type"] == SUFFIX && !phrase_done) {
			connecting = false;
			curphrase = add_suffix(curphrase, word);
		} else if(dict_entry && dict_entry["type"] == NONENDING_PUNCTUATION) {
			connecting = false;
			curphrase += word;
			phrase_done = true;
		} else if(dict_entry && dict_entry["type"] == ENDING_PUNCTUATION) {
			connecting = false;
			curphrase += word;
			capitalize_next = true;
			phrase_done = true;
		} else {
			// if there's a phrase ready to add to the sentence, add it (empties prefix stack)
			if(curphrase && !connecting) {
				sentence = add_to_sentence(sentence, curphrase, sentence_start, prefixstack);
				curphrase = ""
			}

			connecting = false;
			phrase_done = false;
			// start new phrase
			if(dict_entry && dict_entry["type"] == PREFIX) {
				prefixstack.push(word);
			} else {
				curphrase += word;
			}
			if(sentence.length > 0) {
				sentence_start = capitalize_next;
				capitalize_next = false;
			}
		}
	}
	sentence = add_to_sentence(sentence, curphrase, sentence_start, prefixstack);
	return sentence;
}

function sendSentence(words) {
	$.post("api/updatesentence.cgi", {"sesskey": get_sess_key(), "words": words, "roomid": get_room_id()}); 
}

function get_sess_key() {
	return $('#sesskey').val();
}

function get_room_id() {
	return $('#roomid').val();
}

function shouldInsertBefore(target, dropX, dropY, insertee) {
	var db = $('#dropbox')
	var dragCenterX = insertee.position()['left'] + insertee.width() / 2;
	var dragCenterY = insertee.position()['top'] + insertee.height() / 2;
	if (dragCenterX < db.position()['left'] || dragCenterX > db.position()['left'] + db.width()) {
		return false;
	}
	
	var debug = false;
	if (target.text() == 'Impress') debug = true;
	
	if (dragCenterY < target.position()['top'] - 15 || dragCenterY > target.position()['top'] + target.height()) {
		var p = target.prevAll('.wordbox');
		
		if (p.length) {
			p = $(p.get(0));
			if (dragCenterX > p.position()['left'] + p.width() / 2 && 
					target.position()['top'] > p.position()['top']) {
				return true;
			}
		}

		return false
	}
	
	if (dropX < target.position()['left'] + target.width() / 2) {
		return true;
	}
	
	return false;
	/*if(dropY < target.position()['top']) { // top of target is below drop loc
		if(target.position()['top'] != $('#dropbox > .wordbox:first').position()['top']) { // not in the top row
			return true;
		}
	}
	if(dropX > target.position()['left'] + target.width() / 2) { // center of target is to the left of droploc
		return false;
	}
	if(dropY < target.position()['top'] + insertee.height()) { // bottom of target is below drop loc
		return true;
	}
	if(target.position()['top'] == $('#dropbox > .wordbox:last').position()['top']) { // target is in the bottom row
		return true;
	}
	return false;*/
}

var cureventid = 0

function start() {
	$.get("api/join.cgi", {'roomid': get_room_id(), 'sesskey': get_sess_key()}, eventLoop);
	$(window).bind("beforeunload", function() {
		$.post('api/part.cgi', {'sesskey': get_sess_key(), 'roomid': get_room_id()});
	});
	setTimeout(pingLoop, 5000);
}

function scrollChat() {
	if (($("#chatmsgs").attr("scrollHeight") - $("#chatmsgs").attr('scrollTop')) < $("#chatmsgs").height() + 50) {
		$("#chatmsgs").stop(true, true).animate({scrollTop: $("#chatmsgs").attr("scrollHeight")}, 700);
	}
}

function pingLoop() {
	$.post("api/ping.cgi", {'roomid': get_room_id(), 'sesskey': get_sess_key()});
	setTimeout(pingLoop, 5000);
}

function eventLoop() {
	// TODO: find out whether TCP guarantees that the events will arrive in order
	// when you do it asynchronously like this
	// also, ensure that only one event is being applied at any given time
	$.getJSON("api/geteventssince.cgi", {"eventid": cureventid, "roomid": get_room_id()}, function(events) {
		for(var i in events) {
			processEvent(events[i]);
		}
	});
	setTimeout(eventLoop, 1000);
}

function roomlist() {
	resetUi();
	$.getJSON("api/getroomlist.cgi", function(rooms) {
		$("#gamebox").append("<div class='notification' id='rooms><h3>Choose a room to join:</h3></div>");
		for(var roomid in rooms) {
			$("#rooms").append("<p><button name='room" + roomid + "' onclick='selectroom(" + roomid + ")'>" + rooms[roomid] + "</button></p>");
		}
	});
}

function selectroom(roomid) {
	resetUi();
	$("#roomid").val(roomid);
	$("#chat").append(
		$('<div id="chatinput">').append(
			$('<input type="text" id="chatmessage" />').keypress(function(e) {
					if (e.which == 13) sendChat();
			})
		)
	);
	start();
}

$(showLogin);

$(timeLoop);

var timeLeft = 0;
var lastUpdate = 0;
var showTime = false;
function timeLoop() {
	var curTime = new Date().getTime();
	timeLeft = Math.max(0, timeLeft + lastUpdate - curTime);
	lastUpdate = curTime;
	if(showTime) {
		$("#time").text(Math.floor(timeLeft / 1000));
	}
	setTimeout(timeLoop, 100);
}

function setPlayers(players) {
	$("#members").empty();
	for(player in players) {
		var name = players[player];
		$("#members").append("<p id='user_" + name + "' class='username'>" + name + "</p>");
	}
}

function processEvent(ev) {
	eventid = ev["eventid"]
	if(eventid <= cureventid) { // likely with lag of > 1 second
		return;
	}
	cureventid = eventid;
	evtype = ev["type"];
	if(ev["timeleft"]) {
		setTime(ev["timeleft"]);
	}

	if(evtype == "new round" && ev["words"]) {
		startRound();
		setPlayers(ev["players"]);
		insertWords(ev["words"]);
	} else if(evtype == "collecting") {
		startCollecting();
	} else if(evtype == "vote" && ev["sentences"]) {
		startVoting(ev["sentences"]);
	} else if(evtype == "voting over") {
		startCollectingVotes();
	} else if(evtype == "winner" && ev["winner"] && ev["votes"] && ev["scores"]) {
		showWinners(ev["data"]);
	} else if(evtype == "game over" && ev["scores"]) {
		showGameWinners(ev["scores"]);
	} else if(evtype == "join" && ev["name"]) {
		playerJoined(ev["name"]);
	} else if(evtype == "part" && ev["name"]) {
		playerParted(ev["name"]);
	} else if(evtype == "chat" && ev["text"] && ev["username"]) {
		chatMessage(ev["text"], ev["username"]);
	}
}

function setTime(time) {
	timeLeft = Math.max(time * 1000, 0);
	lastUpdate = new Date().getTime();
	showTime = true;
}

function resetUi() {
	$("#gamebox").empty();
	$(".wordbox").remove();
}

function login() {
	var username = $("#username").val();
	var password = $("#password").val();
	$.post("api/login.cgi", {"password": password, "username": username}, function(data, textStatus) {
		if(data && data["status"] && data["status"] == "OK" && data["sesskey"]) {
			$('#sesskey').val(data["sesskey"]);
			$.cookie('amalgam-sesskey', data["sesskey"], {path: '/'});
			roomlist();
		}
	}, "json");
}

function showLogin() {
	resetUi();
	if($.cookie('amalgam-sesskey')) {
		$('#sesskey').val($.cookie('amalgam-sesskey'));
		roomlist();
	} else {
		$("#gamebox").append('<form action="index.cgi" method="post"><p>Username: <input type="text" name="username" id="username" /></p><p>Password: <input type="password" name="password" id="password" /></p><input type="submit" value="Log In" name="submit" onclick="login(); return false" onkeypress="return false" /><p class="notes"><a href="register.cgi">Register</a></p><p class="notes"><a href="forgot.cgi">Forgot your password?</a></p></form>');
	}
}

function startRound() {
	resetUi();
	$("#gamebox")
		.append(
			$("<div class='wordsouter' />")
				.append("<div class='wordsbox wordscontainer' id='wordsbox'></div>")
				.append("<div class='dropbox wordscontainer' id='dropbox'></div>")
				.append("<p class='sentence' id='sentence'>&nbsp;</p>")
			);
	$("#dropbox")
		.append("<div class='prop' id='prop'></div>")
		.append("<div class='clear' id='clear'></div></div>")
		.bind('drop', function(event) {
			// find the farthest-left wordbox with a center to the right of the mouse pointer.
			// insert word before it.
			// note that the wordboxes are already sorted in order for us.
			
			var done = false;
			$.each($('#dropbox > .wordbox'), function(idx, box) {
				if(!done && shouldInsertBefore($(box), event.pageX, event.pageY, $(event.dragTarget))) {
					done = true;
					$(event.dragTarget).insertBefore(box);
				}
			});
			if(!done) {
				$(event.dragTarget).insertBefore($('#clear'))
			}
			$(event.dragTarget).css({
				position: 'static',
				float: 'left'
			});
			$('.spacer').css({width: '0px'}).remove();
			$.dropManage(); // might have resized from adding a fella
			updateSentence();
		});
}

function startCollecting() {
	resetUi();
	$("#gamebox").append("<div class='notification'><p>Collecting sentences!</p></div>");
}

function hashesTo(key, hash) {
	var saltstart = hash.indexOf("$");
	var saltend = hash.lastIndexOf("$");
	if(saltstart == -1) {
		return false;
	}
	var salt = hash.slice(saltstart + 1, saltend);
	var hashpart = hash.slice(saltend + 1);
	if(hex_sha1(salt + key) == hashpart) {
		return true
	}
	return false;
}

function startVoting(sentences) {
	resetUi();
	$("#gamebox").append("<p>voting!</p");
	$("#gamebox").append("<table class='votetable' id='votetable' border=1></table");
	for(sentenceid in sentences) {
		sentence = makeSentence(sentences[sentenceid]);
		if(hashesTo(get_sess_key(), sentenceid)) {
			$("#votetable").append("<tr><td>" + sentence + "</td><td>YOUR SENTENCE</td></tr>");
		} else {
			$("#votetable").append("<tr><td>" + sentence + "</td><td><button onclick=\"$.get('api/vote.cgi', {sentenceid: '" + sentenceid + "', sesskey: '" + get_sess_key() + "'}) \">Vote</button></td></tr>");
		}
	}
}

function startCollectingVotes() {
	resetUi();
	$("#gamebox").append("<div class='notification'><p>Collecting votes!</p></div>");
}

function showWinners(data) {
	// TODO: make this massively more sophisticated
	resetUi();
	$("#gamebox").append("<table class='winners' id='winners'></table>");
	for(name in data) {
		if(data[name]["iswinner"]) {
			$("#gamebox").append("<p>Winner: " + name + "</p>");
		}
		$("#winners").append("<tr><td>" + makeSentence(data[name]["sentence"]) + "</td><td>" + name + "</td><td>" + data[name]["score"] + "</td><td>" + data[name]["votes"]);
	}
}

function showGameWinners(scores) {
	resetUi();
	$("#gamebox").append("<p>GAME OVER!!!</p>");
	for(person in scores) {
		$("#gamebox").append("<p>" + person + " had " + scores[person] + " points.</p>");
	}
}

function playerJoined(name) {
	$("#chatmsgs").append(
		$("<p />").append(
			$("<span />").addClass("chat_action").text(name + " joined.")
		)
	);
	$("#members").append("<p id='user_" + name + "' class='username'>" + name + "</p>");
	scrollChat();
}

function playerParted(name) {
	$("#chatmsgs").append(
		$("<p />").append(
			$("<span />").addClass("chat_action").text(name + " left.")
		)
	);
	$("#user_" + name).remove();
	scrollChat();
}

function chatMessage(message, username) {
	$("#chatmsgs").append(
		$("<p />").append(
			$("<span />").addClass("chat_username").text(username + ":")
		).append(
			$("<span />").addClass("chat_text").text(message)
		)
	)
	scrollChat();
}

function sendChat() {
	$.get("api/chat.cgi", {'roomid': get_room_id(), 'sesskey': get_sess_key(), 'text': get_chat_text()});
	$("#chatmessage").val("");
}

function get_chat_text() {
	return $('#chatmessage').val();
}

var wordlist = [];
var lastClick = null;
var curSpacer;

function insertWords(words) {
	wordlist = words;
	lastClick = null;

	// first, insert all the boxes with static positioning
	for(var i = 0; i < words.length; i++) {
		word = words[i];
		var box = $('<span id="wordbox' + i + '" class="wordbox">' + capitalize_display(word) + '</span>');
		if(word == "==") {
			box.addClass('copy');
		}
		if(word == "==" || word == "^" || word == "++") {
			box.addClass('special');
		}
		$('#wordsbox').append(box);
	}

	// next, save all of their positions
	var box_poses = []
	$.each($('.wordbox'), function(idx, box) {
		box_poses[$(box).attr('id')] = $(box).position(); 
	});

	// then, convert them all to absolute positioning using saved positions
	$.each($('.wordbox'), function(idx, box) {
		$(box).css({
			position: 'absolute',
			top: box_poses[$(box).attr('id')]['top'],
			left: box_poses[$(box).attr('id')]['left'],
			float: 'none'
		});
	});
	$('.wordbox').bind('drag', function(event){
		$(this).css({
			position: 'absolute',
			top: event.offsetY,
			left: event.offsetX
		});

		var done = false;
		var db = $('#dropbox');
		var oldCurSpacer = curSpacer;
		if (event.pageY > db.position()['top'] && event.pageY < db.position()['top'] + db.height()) {
			$.each($('#dropbox > .wordbox'), function(idx, box) {
				if(!done && shouldInsertBefore($(box), event.pageX, event.pageY, $(event.dragTarget))) {
					done = true;
	
					if (curSpacer != box) {
						killSpacers();
						curSpacer = box;
					}
				}
			});
			
			if (!done) {
				curSpacer = $('#clear').get(0);
			}
		} else {
			curSpacer = null;
		}
		
		if (curSpacer != oldCurSpacer) {
			killSpacers();
			
			if (curSpacer != null) {
				$(event.dragTarget).clone()
									.removeClass('wordbox')
									.addClass('spacer')
									.insertBefore(curSpacer)
									.css({
										position: 'static',
										float: 'left',
										width: '0px'
									})
									.animate({width: ($(event.dragTarget).width()) + "px"},	25);
			}			
		}
	})
	.bind('dragstart', function(event) {
		if ($(this).parent().get(0).id == 'dropbox') {
			
			var done = false;
			$.each($('#dropbox > .wordbox'), function(idx, box) {
				if(!done && shouldInsertBefore($(box), event.pageX, event.pageY, $(event.dragTarget))) {
					done = true;
					curSpacer = box;
				}
			});
			if (!done) {
				curSpacer = $("#clear").get(0);
			}
			$(event.dragTarget).clone()
				.removeClass('wordbox')
				.addClass('spacer')
				.insertBefore(curSpacer)
				.css({
					position: 'static',
					float: 'left',
					width: $(event.dragTarget).width() + 'px'
			});
		}
		$('body').append(this); // remove from the sentence
		$.dropManage(); // drop area might have resized from removing it
		updateSentence();
	})
	.bind('dragend', function() {
		killSpacers();
		curSpacer = null;
	})
	.rightClick(function() {
		if($(this).hasClass('copy')) {
			$(this).text("==");
			updateSentence();
		}
	})
	.mousedown(function() {
		if($(this).hasClass('copy') && $(this).text() == "==") {
			if(lastClick) {
				$(this).text(lastClick);
			}
		} else {
			lastClick = $(this).text();
		}
	});
}

function killSpacers() {
	var spacers = $('.spacer')
		spacers.animate({
			width: '0px'
		}, 25, function () {
			spacers.remove();
			$.dropManage(); // might have resized from adding a fella
		});
}