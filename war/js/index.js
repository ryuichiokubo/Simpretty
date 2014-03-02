var ryuichiokubo_simpretty = function() {

  const URL_FEED = 'http://localhost:8888/simpretty';

  // HTML elements
  var el = (function() {
    var main = document.getElementById('main');
    var article = document.getElementsByTagName('article')[0];

    return {
      main: main,
      article: article
    }
  })();

  var renderFeed = function(data) {

    // Larger fonts for more comments
    var getFontSize = function(feed) {
      var size = 1;
      var unit = 'rem';
      var extra = feed.comment * 0.1;
      return size + extra + unit;
    }

    // Append feeds which are sent from server as sorted list
    feeds = JSON.parse(data);
    feeds.forEach(function(feed) {
      var node = el.article.cloneNode();
      var header = node.firstElementChild;
      header.textContent = feed.title;
      header.style.fontSize = getFontSize(feed);
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