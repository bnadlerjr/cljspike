(ns webdev.common.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [javascript-tag]]))

(defn common
  "The main layout. Uses the CDN versions of jQuery and Bootstrap. If those are
  not available, fall back to local copies."
  [& body]
  (html5 {:lang :en}
         [:head
          [:title "Clj Spike"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]

          ;; Bootstrap CSS CDN
          (include-css "//netdna.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")]

         [:body
          body

          ;; jQuery CDN
          (include-js "http://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js")

          ;; jQuery local fallback
          (javascript-tag "window.jQuery || document.write('<script src=\"/jquery-2.1.3.min.js\"><\\/script>')")

          ;; Bootstrap JS CDN
          (include-js "//netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js")

          ;; Bootstrap JS local fallback
          (javascript-tag "if(typeof($.fn.modal) === 'undefined') {document.write('<script src=\"/bootstrap/js/bootstrap.min.js\"><\\/script>')}")

          ;; Bootstrap CSS local fallback
          (javascript-tag
            (str
              "$(document).ready(function() {"
              "var bodyColor = $('body').css('color');"
              "if(bodyColor != 'rgb(51, 51, 51)') {"
              "$(\"head\").prepend('<link rel=\"stylesheet\" href=\"/bootstrap/css/bootstrap.min.css\">');}});"))]))
