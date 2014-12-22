(ns webdev.core-test
  (:require [clojure.test :refer :all]
            [webdev.core :refer [app]]
            [ring.mock.request :as mock]))

(deftest routes
  (testing "/items"
    (let [response (app (mock/request :get "/items"))]
      (is (= 200 (:status response)))))

  (testing "not found"
    (let [response (app (mock/request :get "/no-such-route"))]
      (is (= 404 (:status response))))))
