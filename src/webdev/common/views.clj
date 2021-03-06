(ns webdev.common.views
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [javascript-tag]]))

(def jquery-version "2.1.3")
(def bootstrap-version "3.3.1")

(defn- bootstrap-css-cdn [version]
  (include-css
    (str
      "//netdna.bootstrapcdn.com/bootstrap/"
      version
      "/css/bootstrap.min.css")))

(defn- bootstrap-css-local-fallback [version]
  (javascript-tag
    (str
      "$(document).ready(function() {"
      "var bodyColor = $('body').css('color');"
      "if(bodyColor != 'rgb(51, 51, 51)') {"
      "$(\"head\").prepend('<link rel=\"stylesheet\" href=\"/bootstrap/"
      version
      "/css/bootstrap.min.css\">');}});")))

(defn- bootstrap-js-cdn [version]
  (include-js
    (str
      "//netdna.bootstrapcdn.com/bootstrap/"
      version
      "/js/bootstrap.min.js")))

(defn- bootstrap-js-local-fallback [version]
  (javascript-tag
    (str
      "if(typeof($.fn.modal) === 'undefined') "
      "{document.write('<script src=\"/bootstrap/"
      version
      "/js/bootstrap.min.js\"><\\/script>')}")))

(defn- jquery-cdn [version]
  (include-js
    (str
      "http://ajax.googleapis.com/ajax/libs/jquery/"
      version
      "/jquery.min.js")))

(defn- jquery-local-fallback [version]
  (javascript-tag
    (str
      "window.jQuery || document.write('<script src=\"/jquery-"
      version
      ".min.js\"><\\/script>')")))

(defn layout
  "The main layout. Uses the CDN versions of jQuery and Bootstrap. If those are
  not available, fall back to local copies."
  [& body]
  (html5
    {:lang :en}
    [:head
     [:title "Clj Spike"]
     [:meta {:name :viewport
             :content "width=device-width, initial-scale=1.0"}]
     (bootstrap-css-cdn bootstrap-version)]

    [:body
     body
     (jquery-cdn jquery-version)
     (jquery-local-fallback jquery-version)
     (bootstrap-js-cdn bootstrap-version)
     (bootstrap-js-local-fallback bootstrap-version)
     (bootstrap-css-local-fallback bootstrap-version)]))
