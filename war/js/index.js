var ryuichiokubo_simpretty = function() {

  // 1. (TBD) Load contents from local storage
  // 2. Get contents from server

  const URL_FEED = 'http://localhost:8888/simpretty';

  var xhr = new XMLHttpRequest();
  xhr.onload = function() {
    var feed = xhr.responseText;
    renderFeed(feed);
  }
  xhr.open('GET', URL_FEED);
  xhr.send();

  var mainEl = (function() {
    return document.getElementById('main');
  })();

  var renderFeed = function(feed) {
    mainEl.innerHTML = feed;
  }

}

document.addEventListener( "DOMContentLoaded", ryuichiokubo_simpretty, false );