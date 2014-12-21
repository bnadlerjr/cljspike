(ns webdev.core
  (:require [webdev.model :as model])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]))

(def db "jdbc:postgresql://localhost/webdev")

(defn greet [req]
  {:status 200 :body "Hello, world! Now with reloading!" :headers {}})

(defn goodbye [req]
  {:status 200 :body "Goodbye, cruel world!" :headers {}})

(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (not-found "Page not found."))

(defn -main [port]
  (model/create-table db)
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (model/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
