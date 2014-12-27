(ns webdev.core
  (:use [org.httpkit.server :only [run-server]])
  (:require [webdev.item.migration :as migration]
            [webdev.item.routes :refer [item-routes]]
            [webdev.common.routes :refer [common-routes]]
            [webdev.common.middleware :refer [wrap-db
                                              wrap-simulated-methods]])
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [routes]]
            [environ.core :refer [env]]))

(def db (env :database-url))

(def app
  (wrap-file-info
    (wrap-resource
      (wrap-db
        (wrap-params
          (wrap-simulated-methods (routes item-routes common-routes))))
      "static")))

(defn -main [port]
  (migration/create-table db)
  (run-server app {:port (Integer. port)}))

(defn -dev-main [port]
  (migration/create-table db)
  (run-server (wrap-reload #'app) {:port (Integer. port)}))
