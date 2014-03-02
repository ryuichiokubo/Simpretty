var ryuichiokubo_simpretty = function() {

  const URL_FEED = 'http://localhost:8888/simpretty';

  var el = (function() {
    var main = document.getElementById('main');
    var article = document.getElementsByTagName('article')[0];

    return {
      main: main,
      article: article
    }
  })();

  var renderFeed = function(data) {
    feeds = JSON.parse(data);
    feeds.forEach(function(feed) {
      var node = el.article.cloneNode();
      node.firstElementChild.textContent = feed.title;
      el.main.appendChild(node);
    });

    // remove template node
    el.main.removeChild(el.article);
  }

  var getFeed = function() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      var data = xhr.responseText;
      renderFeed(data);
    }
    xhr.open('GET', URL_FEED);
    xhr.send();
  }


  // 1. (TODO) Load contents from local storage
  // 2. Get contents from server

  getFeed();
}

document.addEventListener( "DOMContentLoaded", ryuichiokubo_simpretty, false );