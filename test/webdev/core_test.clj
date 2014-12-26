(ns webdev.core-test
  (:require [clojure.test :refer :all]
            [webdev.core :refer [app]]
            [webdev.item.migration :as migration]
            [ring.mock.request :as mock]
            [environ.core :refer [env]]))

(defn db-fixture [f]
  (migration/create-table (env :database-url))
  (f))

(use-fixtures :once db-fixture)

(deftest routes
  (testing "/items"
    (let [response (app (mock/request :get "/items"))]
      (is (= 200 (:status response)))))

  (testing "not found"
    (let [response (app (mock/request :get "/no-such-route"))]
      (is (= 404 (:status response))))))
