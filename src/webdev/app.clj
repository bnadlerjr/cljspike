(ns webdev.app
  (:require [webdev.item.migrations :as migration]
            [webdev.item.handlers :refer [item-routes]]
            [webdev.common.handlers :refer [common-routes]]
            [webdev.common.middleware :refer [wrap-db
                                              wrap-simulated-methods]])
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [routes]]
            [environ.core :refer [env]]
            [prone.middleware :as prone]))

(def db (env :database-url))

(def application-defaults
  (assoc-in site-defaults [:static :resources] "static"))

(def application
  (-> (routes
        item-routes
        common-routes)
      (wrap-simulated-methods)
      (wrap-db)
      (wrap-defaults application-defaults)))

(defn -main [port]
  (migration/create-table db)
  (run-server application {:port (Integer. port)}))

(defn -dev-main [port]
  (migration/create-table db)
  (run-server
    (prone/wrap-exceptions
      (wrap-reload #'application)) {:port (Integer. port)}))
