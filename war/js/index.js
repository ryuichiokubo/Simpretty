/*jslint browser:true, indent:4 */
/*global DOMParser*/

(function () {
    "use strict";

    var ryuichiokubo_simpretty = function () {

        //var URL_FEED = 'http://simpretty-rss.appspot.com/simpretty',
        var URL_FEED = 'http://localhost:8888/simpretty',
            el = {},
            tmpl = {},
            renderFeed,
            getFeed;

        // HTML elements
        el = (function () {
            var main = document.getElementById('main');

            return {
                main: main
            };
        }());

        // HTML templates
        tmpl = function (id) {
            var wrapper = document.createElement('div');
            wrapper.innerHTML = document.getElementById(id).firstChild.textContent.replace('\n', '', 'g');
            return wrapper.firstElementChild.cloneNode();
        };

        renderFeed = function (data) {

            // Larger fonts for more comments
            var getFontSize = function (feed) {
                var size = 1,
                    unit = 'rem',
                    extra = Math.log(feed.comment);
                return size + extra + unit;
            },

                // Append feeds which are sent from server as sorted list
                feeds = JSON.parse(data);

            feeds.forEach(function (feed) {
                var article, header, a;

                article = tmpl('article-tmpl');
                header = article.firstElementChild;
                a = header.firstElementChild;

                a.textContent = feed.title;
                a.href = feed.link;
                header.style = {};
                header.style.fontSize = getFontSize(feed);
                el.main.appendChild(article);
            });
        };

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