(ns webdev.core-test
  (:require [clojure.test :refer :all]
            [webdev.core :refer [app]]
            [webdev.item.model :as model]
            [ring.mock.request :as mock]))

(defn db-fixture [f]
  (model/create-table "jdbc:postgresql://localhost/webdev")
  (f))

(use-fixtures :once db-fixture)

(deftest routes
  (testing "/items"
    (let [response (app (mock/request :get "/items"))]
      (is (= 200 (:status response)))))

  (testing "not found"
    (let [response (app (mock/request :get "/no-such-route"))]
      (is (= 404 (:status response))))))
