(ns webdev.core-test
  (:require [clojure.test :refer :all]
            [webdev.core :refer [app]]
            [ring.mock.request :as mock]))

(deftest routes
  (testing "greet"
    (let [response (app (mock/request :get "/"))]
      (is (= 200 (:status response)))
      (is (= "Hello, world! Now with reloading!" (:body response)))))

  (testing "goodbye"
    (let [response (app (mock/request :get "/goodbye"))]
      (is (= 200 (:status response)))
      (is (= "Goodbye, cruel world!" (:body response))))))
