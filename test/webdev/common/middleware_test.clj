(ns webdev.common.middleware-test
  (:require [clojure.test :refer :all]
            [webdev.common.middleware :refer [wrap-db
                                              wrap-simulated-methods]]))

(deftest wrap-db-middleware
  (testing "adds database url to request"
    (let [middleware (wrap-db identity)
          request {}
          response (middleware request)]
      (is (=
            {:webdev/db "jdbc:postgresql://localhost/webdev_test"}
            response)))))

(deftest wrap-simulated-methods-middleware
  (let [middleware (wrap-simulated-methods identity)]
    (testing "POST request with a DELETE param"
      (let [request {:request-method :post
                     :params {"_method" "DELETE"}}
            response (middleware request)]
        (is (= :delete (:request-method response)))))

    (testing "POST request with a PUT param"
      (let [request {:request-method :post
                     :params {"_method" "PUT"}}
            response (middleware request)]
        (is (= :put (:request-method response)))))

    (testing "POST request without a _method"
      (let [request {:request-method :post}
            response (middleware request)]
        (is (= :post (:request-method response)))))

    (testing "POST request with an unknown param"
      (let [request {:request-method :post
                     :params {"_method" "foo"}}
            response (middleware request)]
        (is (= :post (:request-method response)))))))
