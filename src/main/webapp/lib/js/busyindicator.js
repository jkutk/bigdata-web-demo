/**
 * Busy indicator that shows a busy indicator on all clicks
 */

function showBusyIndicator(event, url, retval) {
	$('.busyindicator').show();
	if(url != undefined) {
		if(retval == true) {
			setTimeout(function() {
				document.location = url;
			}, 50);
		}else{
			return false;
		}
	}
	return true;
}

function setupShowBusyIndicator() {
	if ($(this).hasClass('withBusyIndicator')) {
		$(this).click(function() {
			showBusyIndicator();
		});
	}
}

function setupShowBusyIndicatorHref() {
	if ($(this).hasClass('withBusyIndicator')) {
		var url = $(this).attr('href');
		if (this.onclick == undefined) {
			this.onclick = function (event) {
				showBusyIndicator(event, url, true);
			};
		} else {
			var origOnClick = this.onclick;
			if(url != '#') {
				this.onclick = function(event) {
					var retval = origOnClick(event);
					return showBusyIndicator(event, url, retval);
				};
			}else{
				this.onclick = function(event) {
					showBusyIndicator(event, url, true);
					return origOnClick(event);
				};
			}
		}
	}
}

function goUrl(url) {
	showBusyIndicator();
	setTimeout(function() {
		document.location = url;
	}, 100);
}

$(document).ready(function() {
	$('a').each(setupShowBusyIndicatorHref);
	$('button').each(setupShowBusyIndicator);
});
