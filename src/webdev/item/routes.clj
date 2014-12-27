(ns webdev.item.routes
  (:require [compojure.core :refer [GET POST PUT DELETE routes]]
            [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]]))

(def item-routes
  (routes
    (GET "/items" [] handle-index-items)
    (POST "/items" [] handle-create-item)
    (DELETE "/items/:item-id" [] handle-delete-item)
    (PUT "/items/:item-id" [] handle-update-item)))
