(ns webdev.item.handler
  (:require [ring.util.response :refer [response not-found redirect]]
            [webdev.item.model :refer [create-item
                                       read-items
                                       update-item
                                       delete-item]]
            [webdev.item.view :refer [items-page]]))

(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (read-items db)]
    (response (items-page items))))

(defn handle-create-item [req]
  (let [name (get-in req [:params "name"])
        description (get-in req [:params "description"])
        db (:webdev/db req)]
    (create-item db name description)
    (redirect "/items")))

(defn handle-delete-item [req]
  (let [db (:webdev/db req)
        item-id (java.util.UUID/fromString (:item-id (:route-params req)))
        exists? (delete-item db item-id)]
    (if exists?
      (redirect "/items")
      (not-found "Item not found."))))

(defn handle-update-item [req]
  (let [db (:webdev/db req)
        item-id (java.util.UUID/fromString (:item-id (:route-params req)))
        checked (get-in req [:params "checked"])
        exists? (update-item db item-id (= "true" checked))]
    (if exists?
      (redirect "/items")
      (not-found "Item not found."))))
