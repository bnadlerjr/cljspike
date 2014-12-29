(ns webdev.item.handlers
  (:require [ring.util.response :refer [response
                                        not-found
                                        redirect
                                        content-type]]
            [compojure.core :refer [GET POST PUT DELETE defroutes]]
            [webdev.item.models :refer [create-item
                                       read-items
                                       update-item
                                       delete-item]]
            [webdev.item.views :refer [items-page]]))

(def html-content-type "text/html; charset=utf-8")

(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (read-items db)]
    (content-type (response (items-page items))
                  html-content-type)))

(defn handle-create-item [req]
  (let [name (get-in req [:params :name])
        description (get-in req [:params :description])
        db (:webdev/db req)]
    (create-item db name description)
    (content-type (redirect "/items")
                  html-content-type)))

(defn handle-delete-item [req]
  (let [db (:webdev/db req)
        item-id (java.util.UUID/fromString (:item-id (:route-params req)))
        exists? (delete-item db item-id)]
    (content-type (if exists?
                    (redirect "/items")
                    (not-found "Item not found."))
                  html-content-type)))

(defn handle-update-item [req]
  (let [db (:webdev/db req)
        item-id (java.util.UUID/fromString (:item-id (:route-params req)))
        checked (get-in req [:params :checked])
        exists? (update-item db item-id (= "true" checked))]
    (content-type (if exists?
                    (redirect "/items")
                    (not-found "Item not found."))
                  html-content-type)))

(defroutes item-routes
           (GET "/items" [] handle-index-items)
           (POST "/items" [] handle-create-item)
           (DELETE "/items/:item-id" [] handle-delete-item)
           (PUT "/items/:item-id" [] handle-update-item))
