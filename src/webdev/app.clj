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
            [prone.middleware :refer [wrap-exceptions]]))

(def debug-mode? (env :debug false))

(def application-defaults
  (assoc-in site-defaults [:static :resources] "static"))

(def application
  (wrap-defaults
    (cond->
      (routes item-routes common-routes)
      wrap-simulated-methods
      wrap-db
      debug-mode? wrap-exceptions
      debug-mode? wrap-reload)
    application-defaults))

(defn -main [port]
  (migration/create-table (env :database-url))
  (run-server application {:port (Integer. port)}))
