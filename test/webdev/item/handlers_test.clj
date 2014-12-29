(ns webdev.item.handlers-test
  (:require [clojure.test :refer :all]
            [webdev.item.handlers :refer [item-routes]]
            [ring.mock.request :as mock]
            [environ.core :refer [env]]))

(def fake-uuid "cf966956-e0c9-480f-9e6c-79f890f1d3af")

(deftest item-handlers
  (testing "GET /items"
    (with-redefs [webdev.item.models/read-items (fn [_] [])]
      (let [resp (item-routes (mock/request :get "/items"))]
        (is (= 200 (:status resp))))))

  (testing "POST /items"
    (with-redefs [webdev.item.models/create-item (fn [_ _ _] identity)]
      (let [resp (item-routes (mock/request :post "/items"))]
        (is (= 302 (:status resp)))
        (is (= {"Location" "/items"} (:headers resp))))))

  (testing "successful PUT /items"
    (with-redefs [webdev.item.models/update-item (fn [_ _ _] true)]
      (let [resp (item-routes (mock/request :put (str "/items/" fake-uuid)))]
        (is (= 302 (:status resp)))
        (is (= {"Location" "/items"} (:headers resp))))))

  (testing "unsuccessful PUT /items/item-id"
    (with-redefs [webdev.item.models/update-item (fn [_ _ _] false)]
      (let [resp (item-routes (mock/request :put (str "/items/" fake-uuid)))]
        (is (= 404 (:status resp)))
        (is (= {} (:headers resp)))
        (is (.contains "Item not found." (:body resp))))))

  (testing "successful DELETE /items/:item-id"
    (with-redefs [webdev.item.models/delete-item (fn [_ _] true)]
      (let [resp (item-routes (mock/request :delete (str "/items/" fake-uuid)))]
        (is (= 302 (:status resp)))
        (is (= {"Location" "/items"} (:headers resp))))))

  (testing "unsuccessful DELETE /items/:item-id"
    (with-redefs [webdev.item.models/delete-item (fn [_ _] false)]
      (let [resp (item-routes (mock/request :delete (str "/items/" fake-uuid)))]
        (is (= 404 (:status resp)))
        (is (= {} (:headers resp)))
        (is (.contains "Item not found." (:body resp)))))))
