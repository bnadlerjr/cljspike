(ns webdev.core
  (:require [webdev.model :as model])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]))

(def db "jdbc:postgresql://localhost/webdev")

(defn greet [req]
  {:status 200 :body "Hello, world! Now with reloading!" :headers {}})

(defn goodbye [req]
  {:status 200 :body "Goodbye, cruel world!" :headers {}})

(defroutes routes
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (not-found "Page not found."))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db))))

(defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Webdev Spike")))

(def app
  (wrap-server (wrap-db (wrap-params routes))))

(defn -main [port]
  (model/create-table db)
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (model/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
