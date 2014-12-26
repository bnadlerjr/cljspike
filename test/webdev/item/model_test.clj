(ns webdev.item.model-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [webdev.item.model :as model]
            [webdev.item.migration :as migration]
            [environ.core :refer [env]]))

(def db (env :database-url))

(defn db-fixture [f]
  (migration/create-table (env :database-url))
  (f))

(use-fixtures :once db-fixture)

(deftest update-item-test
  (jdbc/with-db-transaction
    [transaction db]
    (jdbc/db-set-rollback-only! transaction)
    (let [id (:id
               (first
                 (jdbc/insert!
                   transaction :items
                   {:name "Test Item" :description "A sample test item."})))
          invalid-id #uuid"cf966956-e0c9-480f-9e6c-79f890f1d3af"
          result (model/update-item transaction id true)]

      (testing "returns true if item was updated"
        (is (true? result)))

      (testing "updates 'checked' column in database"
        (is (true? (:checked
                     (first
                       (jdbc/query
                         transaction
                         ["SELECT checked FROM items WHERE id = ?" id]))))))

      (testing "returns false if item was not found"
        (is (false? (model/update-item transaction invalid-id true)))))))

(deftest delete-item-test
  (jdbc/with-db-transaction
    [transaction db]
    (jdbc/db-set-rollback-only! transaction)
    (let [id (:id
               (first
                 (jdbc/insert!
                   transaction :items
                   {:name "Test Item" :description "A sample test item."})))
          result (model/delete-item transaction id)]

      (testing "returns true if item was deleted"
        (is (true? result)))

      (testing "deletes item from database"
        (is (= [] (jdbc/query transaction ["SELECT id FROM items"]))))

      (testing "returns false if item was not found"
        (is (false? (model/delete-item transaction id)))))))

(deftest read-items-test
  (jdbc/with-db-transaction
    [transaction db]
    (jdbc/db-set-rollback-only! transaction)
    (jdbc/insert!
      transaction :items
      {:name "Fourth" :date_created (java.sql.Timestamp/valueOf "2014-12-31 10:23:54") :description "This should be the fourth item."}
      {:name "Third" :date_created (java.sql.Timestamp/valueOf "2014-12-25 10:23:54") :description "This should be the third item."}
      {:name "First" :date_created (java.sql.Timestamp/valueOf "2014-12-19 10:23:54") :description "This should be the first item."}
      {:name "Second" :date_created (java.sql.Timestamp/valueOf "2014-12-21 10:23:54") :description "This should be the second item."})

    (testing "returns correct columns"
      (let [expected '(:date_created :checked :description :name :id)]
        (is (= expected (keys (first (model/read-items transaction)))))))

    (testing "returns items sorted by date created"
      (let [expected '("First" "Second" "Third" "Fourth")]
        (is (= expected (map :name (model/read-items transaction))))))))

(deftest create-item-test
  (jdbc/with-db-transaction
    [transaction db]
    (jdbc/db-set-rollback-only! transaction)
    (let [id (model/create-item transaction "Item" "Some item.")
          item (first
                 (jdbc/query
                   transaction
                   ["SELECT * FROM items WHERE id = ?" id]))]

      (testing "returns item ID"
        (is (= id (:id item))))

      (testing "defaults 'checked' to 'false'"
        (is (false? (:checked item))))

      (testing "assigns 'date_created'"
        (is (not (nil? (:date_created item))))))))
