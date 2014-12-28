(ns webdev.common.middleware
  (:require [environ.core :refer [env]]))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db (env :database-url)))))

(def sim-methods {"PUT" :put "DELETE" :delete})

(defn wrap-simulated-methods [hdlr]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (hdlr (assoc req :request-method method))
      (hdlr req))))
