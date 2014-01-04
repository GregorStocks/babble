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
      {:body (generate-string m)})

(defn login [request]
  (log/info "its working.......")
  (response {"status" "OK"
             "sesskey" (str (rand-int 10))}))

(defn stub [request]
  (response {"status" "OK"}))

(defn get-events-since [request]
  (response {"status" "OK"
             "events" []}))

(defn getstate [request]
  (response {"status" "OK"
             "state" {"event" nil
                      "eventid" 0
                      :scores {}
                      :players []}}))

(defn getroomlist [request]
  (response {"status" "OK"
             "rooms" {69 {"name" "Poop room"
                          "users" []}}}))

(defroutes main-routes
  (GET "/" [] (redirect "index.html"))
  (POST "/api/login.cgi" [] login)
  (POST "/api/chat.cgi" [] stub)
  (GET "/api/geteventssince.cgi" [] get-events-since)
  (POST "/api/vote.cgi" [] stub)
  (POST "/api/updatesentence.cgi" [] stub)
  (POST "/api/ping.cgi" [] stub)
  (POST "/api/part.cgi" [] stub)
  (POST "/api/join.cgi" [] stub)
  (GET "/api/getstate.cgi" [] getstate)
  (GET "/api/getroomlist.cgi" [] getroomlist)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
