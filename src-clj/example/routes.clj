(ns example.routes
  (:use compojure.core
        example.views
        [ring.util.response :only (redirect)]
        [cheshire.core :only (generate-string)]
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [clojure.tools.logging :as log]
            [compojure.handler :as handler]
            [compojure.response :as response]))

(defn response [m]
      {:body (generate-string m))

(defn login [request]
  (log/info "its working.......")
  {:body (generate-string {"status" "OK"
                           "sesskey" (str (rand-int 10))})})

(defn chat [request]
  {"status" "OK"})

(defn get-events-since [request]
  {"status" "OK"
   "events" []})


(defroutes main-routes
  (GET "/" [] (redirect "index.html"))
  (POST "/api/login.cgi" [] login)
  (POST "/api/chat.cgi" [] chat)
  (POST "/api/geteventssince.cgi" [] get-events-since)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
