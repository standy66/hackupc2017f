function initMap() {
	var uluru = {lat: 41.387427, lng: 2.11299};
	var map = new google.maps.Map(document.getElementById('map'), {
		zoom: 4,
		center: uluru
	});
	var marker = new google.maps.Marker({
		position: uluru,
		map: map
	});
}

$(function(){
	$("#button-send").click(function() {
		$.getJSON('query', {'number': $('#input-number').val()}, function(data) {
			if (data['status']) {
				$("#ret-status").html('We found your number!');
				$("#car-photo").attr('src', 'data:image/png;base64,' + data['result']['photo']);
				$('#map').css('opacity', '1');
			}
			else {
				$("#ret-status").html('Your number is not found yet. We will notify you in case we catch it.');
				$("#car-photo").attr('src', '');
				$('#map').css('opacity', '0');
			}
		});
	});
});

