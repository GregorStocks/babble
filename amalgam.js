function add_suffix(phrase, suffix) {
	var suff = suffix.replace(/^-(.*)$/, "$1");
	return phrase + suff;
}

function add_prefix(phrase, prefix) {
	var pref = prefix.replace(/^(.*)-$/, "$1");
	return pref + phrase;
}

function log(text) {
	$('#log').append('<p>' + text + '</p>');
}

function capitalize(word) {
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
		words.push($(box).text())
	});

	var prefixstack = [];
	var sentence = "";
	var curphrase = "";
	var sentence_start = true;
	var capitalize_next = false;
	for(var wordnum in words) {
		var word = words[wordnum];
		var dict_entry = dictionary[word];
		if(dictionary[curphrase] && dictionary[curphrase][word]) { // a special way to combine
			curphrase = dictionary[curphrase][word];
		} else if(dict_entry && dict_entry["type"] == SUFFIX) {
			curphrase = add_suffix(curphrase, word);
		} else if(dict_entry && dict_entry["type"] == NONENDING_PUNCTUATION) {
			curphrase += word;
		} else if(dict_entry && dict_entry["type"] == ENDING_PUNCTUATION) {
			 curphrase += word;
			 capitalize_next = true;
		} else {
			// if there's a phrase ready to add to the sentence, add it (empties prefix stack)
			if(curphrase) {
				sentence = add_to_sentence(sentence, curphrase, sentence_start, prefixstack);
			}

			// start new phrase
			if(dict_entry && dict_entry["type"] == PREFIX) {
				curphrase = ""
				prefixstack.push(word);
			} else {
				curphrase = word;
			}
			if(sentence.length > 0) {
				sentence_start = capitalize_next;
				capitalize_next = false;
			}
		}
	}
	sentence = add_to_sentence(sentence, curphrase, sentence_start, prefixstack);
	$('#sentence').append(sentence);
}

function shouldInsertBefore(target, dropX, dropY, insertee) {
	var targpos = target.position();
	var targwidth = target.width
	if(dropY < target.position()['top']) { // top of target is below drop loc
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
	return false;
}

$(function(){
	$('#dropbox').bind('drop', function(event) {
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
		$.dropManage(); // might have resized from adding a fella
		updateSentence();
	});
	$.getJSON("getwordlist.php", function(wordlist) {
		insertWords(wordlist);
	});
});

function insertWords(wordlist) {
	// first, insert all the boxes with static positioning
	for(var i = 0; i < wordlist.length; i++) {
		var box = $('<span id="wordbox' + i + '" class="wordbox">' + wordlist[i] + '</span>');
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
	})
	.bind('dragstart', function(event) {
		$('body').append(this); // remove from the sentence
		$.dropManage(); // drop area might have resized from removing it
		updateSentence();
	});
}
