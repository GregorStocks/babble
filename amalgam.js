var words = ["butts",  "bums", "buttes", "frankly i am insulted by the preceding terms", "butt", "-y", "-s", "hurf", ",", "!", ".", "this is a very long word! why its not even a word at all!", "test", "bums", "test test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"];

function updateSentence() {
	$('#sentence').empty();
	$.each($('#dropbox > .wordbox'), function(idx, box) {
		$('#sentence').append($(box).text() + " ");
	});
}

$(function(){
	$('#dropbox').bind('drop', function(event) {
		// find the farthest-left wordbox with a center to the right of the mouse pointer.
		// insert word before it.
		// note that the wordboxes are already sorted in order for us.
		var done = false;
		$.each($('#dropbox > .wordbox'), function(idx, box) {
			if(!done && $(box).position()['left'] + $(box).width() / 2 > event.pageX) {
				done = true;
				if(box != event.dragTarget) {
					// can't insert it before itself, just don't do anything instead
					$(event.dragTarget).insertBefore(box);
				}
			}
		});
		if(!done) {
			$(this).append(event.dragTarget);
		}
		$(event.dragTarget).css({
			position: 'static',
		});
		updateSentence();
	});

	// first, insert all the boxes with static positioning
	for(var i = 0; i < words.length; i++) {
		var box = $('<span id="wordbox' + i + '" class="wordbox">' + words[i] + '</span>');
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
		updateSentence();
	});
});
