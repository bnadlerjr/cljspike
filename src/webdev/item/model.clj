(ns webdev.item.model
  (:require [clojure.java.jdbc :as jdbc]
            [java-jdbc.sql :as sql]))

(defn create-table [db]
  (jdbc/execute!
    db
    ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (jdbc/execute!
    db
    ["CREATE TABLE IF NOT EXISTS items
    (id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    checked BOOLEAN NOT NULL DEFAULT FALSE,
    date_created TIMESTAMPTZ NOT NULL DEFAULT now())"]))

(defn create-item [db name description]
  (:id (first (jdbc/insert! db :items {:name name :description description}))))

(defn update-item [db id checked]
  (= [1] (jdbc/update! db :items {:checked checked} ["id = ?" id])))

(defn delete-item [db id]
  (= [1] (jdbc/delete! db :items ["id = ?" id])))

(defn read-items [db]
  (jdbc/query
    db
    (sql/select [:id :name :description :checked :date_created]
                :items
                (sql/order-by :date_created))))
