/*jslint browser:true */

(function () {
    "use strict";
  
    var ryuichiokubo_simpretty = function () {

        var URL_FEED = 'http://localhost:8888/simpretty',
        //var URL_FEED = 'http://simpretty-rss.appspot.com/simpretty';

            // HTML elements
            el = (function () {
                var main = document.getElementById('main'),
                    article = document.getElementsByTagName('article')[0];

                return {
                    main: main,
                    article: article
                };
            }()),

            renderFeed = function (data) {

                // Larger fonts for more comments
                var getFontSize = function (feed) {
                    var size = 1,
                        unit = 'rem',
                        extra = feed.comment * 0.1;
                    return size + extra + unit;
                },

                    // Append feeds which are sent from server as sorted list
                    feeds = JSON.parse(data);
              
                feeds.forEach(function (feed) {
                    var node = el.article.cloneNode(),
                        header = node.firstElementChild,
                        a = header.firstElementChild;
                    a.textContent = feed.title;
                    a.href = feed.link;
                    header.style.fontSize = getFontSize(feed);
                    el.main.appendChild(node);
                });

                // Remove template node
                el.main.removeChild(el.article);
            },

            getFeed = function () {
                var xhr = new XMLHttpRequest();
                xhr.onload = function () {
                    var data = xhr.responseText;
                    renderFeed(data);
                };
                xhr.open('GET', URL_FEED);
                xhr.send();
            };


        // 1. (TODO) Load contents from local storage
        // 2. Get contents from server

        getFeed();
    };

    document.addEventListener("DOMContentLoaded", ryuichiokubo_simpretty, false);
}());