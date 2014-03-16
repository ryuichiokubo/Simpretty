/*jslint browser:true, indent:4 */
/*global DOMParser*/

(function () {
    "use strict";

    var ryuichiokubo_simpretty = function () {

        //var HOST = 'http://simpretty-rss.appspot.com',
        var HOST = 'http://localhost:8888',
            URL_FEED = HOST + '/feed',
            URL_SCORE = HOST + '/score',
            el = {},
            tmpl = {},
            renderFeed,
            getFeed,
            getAjax,
            getScore;

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

        getAjax = function (url, cb) {
            var xhr = new XMLHttpRequest();
            xhr.onload = function () {
                var data = xhr.responseText;
                cb(data);
            };
            xhr.open('GET', url);
            xhr.send();
        };

        getFeed = function () {
            getAjax(URL_FEED, renderFeed)
        };

        getScore = function () {
            getAjax(URL_SCORE, console.log)
        };

        // 1. (TODO) Load contents from local storage
        // 2. Get contents from server
        getFeed();

        // XXX UI
        getScore();
    };

    document.addEventListener("DOMContentLoaded", ryuichiokubo_simpretty, false);
}());