var marker;
var map;
var coordinates;
var originalCoordinates;
var animButton;
var timestamp;

function categoryIcon(category) {
  return '/img/' + category + '.png';
}

function getDelay(category) {
  var delay;
  switch(category) {
    case "car" : delay = 10; break;
    case "walk": delay = 200; break;
    case "hotel": delay = 800; break;
    default: delay = 500;
  }
  return delay;
}

function copy(o) {
  return JSON.parse(JSON.stringify(o));
}

function initMap() {

  fetch('/coordinates')
  .then(function(response) {
    return response.json();
  })
  .then(function(json) {
    coordinates = json.coordinates;
    originalCoordinates = copy(coordinates);

    map = new google.maps.Map(
      document.getElementById('map'), {
        zoom: 9,
        center: json.center,
        mapTypeId: 'terrain'
      }
    );

    new google.maps.Polyline({
        path: coordinates,
        geodesic: true,
        strokeColor: '#2020B0',
        strokeOpacity: 0.8,
        strokeWeight: 6,
        map: map
    });

    var pos = coordinates.shift();
    marker = new google.maps.Marker({
      position: pos,
      map: map,
      icon: categoryIcon(pos.category)
    });

    timestamp = document.getElementById('timestamp');
    timestamp.textContent = pos.timestamp;
  });

  animButton = document.getElementById('animButton');
}

function animateStep() {
  if (coordinates && coordinates.length > 0) {
    var pos = coordinates.shift();
    marker.setPosition(pos);
    marker.setIcon(categoryIcon(pos.category));
    timestamp.textContent = pos.timestamp;
    if (animButton.textContent == "❙❙") {
      setTimeout(animateStep, getDelay(pos.category));
    }
  } else {
    animButton.textContent = "▶︎";
    coordinates = copy(originalCoordinates);
  }
}

function startStopAnimate() {
  if (animButton.textContent == "▶︎") {
    console.log("Animate!");
    animButton.textContent = "❙❙";
    animateStep();
  } else {
    console.log("Pause animation!");
    animButton.textContent = "▶︎";
  }
}

function restartAnimation() {
  coordinates = copy(originalCoordinates);
  animateStep();
}
