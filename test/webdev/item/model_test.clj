(ns webdev.item.model-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [webdev.item.model :as model]
            [webdev.item.migration :as migration]
            [environ.core :refer [env]]))

(declare ^:dynamic *txn*)

(use-fixtures :once
              (fn [f]
                (migration/create-table (env :database-url))
                (f)))

(use-fixtures :each
              (fn [f]
                (jdbc/with-db-transaction
                  [transaction (env :database-url)]
                  (jdbc/db-set-rollback-only! transaction)
                  (binding [*txn* transaction] (f)))))

(deftest create-item-test
  (let [id (model/create-item *txn* "Item" "Some item.")
        item (first
               (jdbc/query
                 *txn*
                 ["SELECT * FROM items WHERE id = ?" id]))]

    (testing "returns item ID"
      (is (= id (:id item))))

    (testing "defaults 'checked' to 'false'"
      (is (false? (:checked item))))

    (testing "assigns 'date_created'"
      (is (not (nil? (:date_created item)))))))

(deftest read-items-test
  (jdbc/insert!
    *txn* :items
    {:name "Fourth"
     :date_created (java.sql.Timestamp/valueOf "2014-12-31 10:23:54")
     :description "This should be the fourth item."}
    {:name "Third"
     :date_created (java.sql.Timestamp/valueOf "2014-12-25 10:23:54")
     :description "This should be the third item."}
    {:name "First"
     :date_created (java.sql.Timestamp/valueOf "2014-12-19 10:23:54")
     :description "This should be the first item."}
    {:name "Second"
     :date_created (java.sql.Timestamp/valueOf "2014-12-21 10:23:54")
     :description "This should be the second item."})

  (testing "returns correct columns"
    (let [expected '(:date_created :checked :description :name :id)]
      (is (= expected (keys (first (model/read-items *txn*)))))))

  (testing "returns items sorted by date created"
    (let [expected '("First" "Second" "Third" "Fourth")]
      (is (= expected (map :name (model/read-items *txn*)))))))

(deftest update-item-test
  (let [id (:id
             (first
               (jdbc/insert!
                 *txn* :items
                 {:name "Test Item" :description "A sample test item."})))
        invalid-id #uuid"cf966956-e0c9-480f-9e6c-79f890f1d3af"
        result (model/update-item *txn* id true)]

    (testing "returns true if item was updated"
      (is (true? result)))

    (testing "updates 'checked' column in database"
      (is (true? (:checked
                   (first
                     (jdbc/query
                       *txn*
                       ["SELECT checked FROM items WHERE id = ?" id]))))))

    (testing "returns false if item was not found"
      (is (false? (model/update-item *txn* invalid-id true))))))

(deftest delete-item-test
  (let [id (:id
             (first
               (jdbc/insert!
                 *txn* :items
                 {:name "Test Item" :description "A sample test item."})))
        result (model/delete-item *txn* id)]

    (testing "returns true if item was deleted"
      (is (true? result)))

    (testing "deletes item from database"
      (is (= [] (jdbc/query *txn* ["SELECT id FROM items"]))))

    (testing "returns false if item was not found"
      (is (false? (model/delete-item *txn* id))))))
