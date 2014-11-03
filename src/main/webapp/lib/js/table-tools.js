/**
 * Tools for handling tables more efficiently
 */

// Execute unobstrusive JS on document.ready
$(document).ready(function() {
	setupClickableCell();
});

function setupClickableCell() {
	$('.clickableCell').each(function (index, value) {
		$(value).click(function(event) {
			$(event.target).find('a').click();
		});
	});
};