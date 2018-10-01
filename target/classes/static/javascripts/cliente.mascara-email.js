$(function() {
	$('#email').mask("A", {
	    translation: {
	        "A": { pattern: /[\w@\-.+]/, recursive: true }
	    }
	});
});