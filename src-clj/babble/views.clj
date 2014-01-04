(ns babble.views
  (:require
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js]]]))

(defn register-page []
  (html5
    [:head
      [:title "Register page for you to register on"]
      (include-js "/js/main.js")]
    [:body
      [:h1 "Hello World"]]))
