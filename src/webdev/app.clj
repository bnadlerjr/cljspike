(ns webdev.app
  (:require [webdev.item.migrations :as migration]
            [webdev.item.routes :refer [item-routes]]
            [webdev.common.routes :refer [common-routes]]
            [webdev.common.middleware :refer [wrap-db
                                              wrap-simulated-methods]])
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [routes]]
            [environ.core :refer [env]]))

(def db (env :database-url))

(def app-routes
  (-> (routes item-routes
              common-routes)
      (wrap-simulated-methods)
      (wrap-params)
      (wrap-db)
      (wrap-resource "static")
      (wrap-file-info)))

(defn -main [port]
  (migration/create-table db)
  (run-server app-routes {:port (Integer. port)}))

(defn -dev-main [port]
  (migration/create-table db)
  (run-server (wrap-reload #'app-routes) {:port (Integer. port)}))
