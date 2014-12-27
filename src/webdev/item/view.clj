(ns webdev.item.view
  (:require [hiccup.core :refer [h]]
            [hiccup.form :refer [form-to
                                 submit-button
                                 hidden-field
                                 label
                                 text-field]]
            [webdev.common.views :refer [layout]]))

(defn- update-item-form [id checked]
  (form-to [:put (str "/items/" id)]
           (hidden-field "checked" (if checked "false" "true"))
           [:div.btn-group
            (submit-button
              {:class "btn btn-primary btn-xs"} (if checked "DONE" "TODO"))]))

(defn- delete-item-form [id]
  (form-to [:delete (str "/items/" id)]
           [:div.btn-group
            (submit-button {:class "btn btn-danger btn-xs"} "Delete")]))

(defn- new-item-form []
  (form-to {:class "form-horizontal"} [:post "/items"]

           [:div.form-group
            (label {:class "control-label col-sm-2"} "name-input" "Name")
            [:div.col-sm-10
             (text-field
               {:id "name-input"
                :class "form-control"
                :placeholder "Name"}
               "name")]]

           [:div.form-group
            (label
              {:class "control-label col-sm-2"}
              "desc-input"
              "Description")
            [:div.col-sm-10
             (text-field
               {:id "desc-input"
                :class "form-control"
                :placeholder "Description"}
               "description")]]

           [:div.form-group
            [:div.col-sm-offset-2.col-sm-10
             (submit-button {:class "btn btn-primary"} "New Item")]]))

(defn items-page [items]
  (layout
    [:div.container
     [:h1 "My Items"]
     [:div.row
      (if (seq items)
        [:table.table.table-striped
         [:thead
          [:tr
           [:th.col-sm-2]
           [:th.col-sm-2]
           [:th "Name"]
           [:th "Description"]]]
         [:tbody
          (for [item items]
            [:tr
             [:td (delete-item-form (:id item))]
             [:td (update-item-form (:id item) (:checked item))]
             [:td (h (:name item))]
             [:td (h (:description item))]])]]
        [:div.col-sm-offset-1 "There are no items."])]
     [:div.col-sm-6
      [:h2 "Create a new item"]
      (new-item-form)]]
    ))
