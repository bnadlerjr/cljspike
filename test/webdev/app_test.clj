(ns webdev.app-test
  (:require [clojure.test :refer :all]
            [webdev.app :refer [application]]
            [webdev.item.migrations :as migration]
            [ring.mock.request :as mock]
            [environ.core :refer [env]]))

(defn db-fixture [f]
  (migration/create-table (env :database-url))
  (f))

(use-fixtures :once db-fixture)

(deftest routes
  (testing "/items"
    (let [response (application (mock/request :get "/items"))]
      (is (= 200 (:status response)))))

  (testing "not found"
    (let [response (application (mock/request :get "/no-such-route"))]
      (is (= 404 (:status response))))))
