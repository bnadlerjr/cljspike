(ns webdev.common.handlers
  (:require [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [ANY routes]]
            [compojure.route :refer [not-found]]))

(def common-routes
  (routes
    (ANY "/request" [] handle-dump)
    (not-found "Page not found.")))
