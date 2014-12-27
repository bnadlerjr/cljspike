(ns webdev.core
  (:use [org.httpkit.server :only [run-server]])
  (:require [webdev.item.migration :as migration]
            [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]])
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [environ.core :refer [env]]))

(def db (env :database-url))

(defroutes routes
  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (DELETE "/items/:item-id" [] handle-delete-item)
  (PUT "/items/:item-id" [] handle-update-item)
  (ANY "/request" [] handle-dump)
  (not-found "Page not found."))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db))))

(def sim-methods {"PUT" :put "DELETE" :delete})

(defn wrap-simulated-methods [hdlr]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (hdlr (assoc req :request-method method))
      (hdlr req))))

(def app
  (wrap-file-info
    (wrap-resource
      (wrap-db
        (wrap-params
          (wrap-simulated-methods routes)))
      "static")))

(defn -main [port]
  (migration/create-table db)
  (run-server app {:port (Integer. port)}))

(defn -dev-main [port]
  (migration/create-table db)
  (run-server (wrap-reload #'app) {:port (Integer. port)}))
