(ns example.routes
  (:use compojure.core
        example.views
        [ring.util.response :only (redirect)]
        [cheshire.core :only (generate-string)]
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [clojure.tools.logging :as log]
            [compojure.handler :as handler]
            [clj-time.core :as time]
            [compojure.response :as response]))

(defonce EVENTS (atom [{:eventid 69
                        :room 69
                        :type "new round"
                        :timeleft 69
                        :words ["ass" "poop" "butt" "bort" (str (rand-int 100))]}]))
(defonce USERS (atom []))
(defonce ROOMS (atom {69 {"name" "Poop room"
                          "users" []}
                      70 {"name" "Boob room"
                          "users" []}}))

(defn ->long [x]
  (Long. (str x)))

(defn response [m]
      {:body (generate-string m)})

(defn stub [request]
  (response {"status" "OK"}))

(defn new-event [m]
  (merge m {:eventid (.getMillis (time/now))}))

(defn login [request]
  (let [username ((:form-params request) "username")]
    (response {"status" "OK"
               "sesskey" username})))

(defn send-chat-message [request]
  (let [params (:form-params request)
        username (params "sesskey")
        text (params "text")
        roomid (->long (params "roomid"))]
    (log/info "Chat message:" username text roomid)
    (swap! EVENTS conj (new-event {:type "chat"
                                   :username username
                                   :text text})))
  (stub request))

(defn get-events-since [request]
  (let [eventid (->long ((:query-params request) "eventid"))]
    (log/info "HEY" eventid (map :eventid @EVENTS) (pr-str @EVENTS ) (filter #(> (:eventid %) eventid) @EVENTS))
    (response {"status" "OK"
               "events" (filter #(> (:eventid %) eventid) @EVENTS)})))

(defn getstate [request]
  (response {"status" "OK"
             "state" {"event" {"eventid" 69
                               "type" "new round"
                               "timeleft" 69
                               "words" ["ass" "poop" "butt" "bort" (str (rand-int 100))]}
                      "eventid" 69
                      :scores {}
                      :players []}}))

(defn getroomlist [request]
  (response {"status" "OK"
             "rooms" @ROOMS}))

(defn join [request]
  (let [username ((:form-params request) "sesskey")]
    (swap! EVENTS conj (new-event {:type "join"
                                   :score 0
                                   :name username}))
    (stub request)))

(defroutes main-routes
  (GET "/" [] (redirect "index.html"))
  (POST "/api/login.cgi" [] login)
  (POST "/api/chat.cgi" [] send-chat-message)
  (GET "/api/geteventssince.cgi" [] get-events-since)
  (POST "/api/vote.cgi" [] stub)
  (POST "/api/updatesentence.cgi" [] stub)
  (POST "/api/ping.cgi" [] stub)
  (POST "/api/part.cgi" [] stub)
  (POST "/api/join.cgi" [] join)
  (GET "/api/getstate.cgi" [] getstate)
  (GET "/api/getroomlist.cgi" [] getroomlist)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
