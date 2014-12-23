(ns webdev.item.model-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [webdev.item.model :as model]))

(def db "jdbc:postgresql://localhost/webdev")

(defn db-fixture [f]
  (model/create-table "jdbc:postgresql://localhost/webdev")
  (f))

(use-fixtures :once db-fixture)

(deftest read-items-test
  (testing "returns correct columns"
    (let [expected '(:date_created :checked :description :name :id)]
      (jdbc/with-db-transaction
        [transaction db]
        (jdbc/db-set-rollback-only! transaction)
        (jdbc/insert!
          transaction :items
          {:name "Test" :description "A test item."})
        (is (= expected (keys (first (model/read-items transaction))))))))

  (testing "returns items sorted by date created"
    (let [expected '("First" "Second" "Third" "Fourth")]
      (jdbc/with-db-transaction
        [transaction db]
        (jdbc/db-set-rollback-only! transaction)
        (jdbc/insert!
          transaction :items
          {:name "Fourth" :date_created (java.sql.Timestamp/valueOf "2014-12-31 10:23:54") :description "This should be the fourth item."}
          {:name "Third" :date_created (java.sql.Timestamp/valueOf "2014-12-25 10:23:54") :description "This should be the third item."}
          {:name "First" :date_created (java.sql.Timestamp/valueOf "2014-12-19 10:23:54") :description "This should be the first item."}
          {:name "Second" :date_created (java.sql.Timestamp/valueOf "2014-12-21 10:23:54") :description "This should be the second item."})
        (is (= expected (map :name (model/read-items transaction))))))))
