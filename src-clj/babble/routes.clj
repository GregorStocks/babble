(ns babble.routes
  (:use compojure.core
        babble.views
        [ring.util.response :only (redirect)]
        [cheshire.core :only (generate-string)]
        [clojure.contrib.generic.functor :only (fmap)]
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [clojure.tools.logging :as log]
            [compojure.handler :as handler]
            [clj-time.core :as time]
            [compojure.response :as response]))

(defn initial-event [roomid]
  {:eventid 1
   :room roomid
   :type "new round"
   :timeleft 0
   :words ["ass" "poop" "butt" "bort"]})

(defn empty-room [name roomid]
  {:name name
   :users #{}
   :events [(initial-event roomid)]
   :event (initial-event roomid)
   :eventid 1
   :scores {}})

(defonce USERS (atom []))
(defonce ROOMS (atom {69 (empty-room "Poop room" 69)
                      70 (empty-room "Boob room" 70)}))

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
    (log/info "#" roomid " <" username "> " text)
    (swap! ROOMS update-in [roomid :events] conj (new-event {:type "chat"
                                                             :username username
                                                             :text text})))
  (stub request))

(defn get-events-since [request]
  (let [eventid (->long ((:query-params request) "eventid"))
        roomid (->long ((:query-params request) "roomid"))]
    (response {"status" "OK"
               "events" (filter #(> (:eventid %) eventid)
                                (:events (@ROOMS roomid)))})))

(defn getstate [request]
  (let [roomid (->long ((:query-params request) "roomid"))]
    (response {"status" "OK"
               "state" (select-keys (@ROOMS roomid) [:users :scores :event :eventid])})))

(defn getroomlist [request]
  (log/info @ROOMS)
  (response {"status" "OK"
             "rooms" (fmap #(select-keys % [:name :users]) @ROOMS)}))

(defn join [request]
  (let [params (:form-params request)
        username (params "sesskey")
        roomid (->long (params "roomid"))
        event (new-event {:type "join"
                          :score 0
                          :name username})]
    (swap! ROOMS #(-> %
                      (update-in [roomid :users] conj username)
                      (update-in [roomid :events] conj event)
                      (update-in [roomid :eventid] (constantly (:eventid event)))
                      (update-in [roomid :scores username] (fn [score] (or score 0)))))
    (stub request)))

(defn part [request]
  (let [params (:form-params request)
        username (params "sesskey")
        roomid (->long (params "roomid"))
        event (new-event {:type "part"
                          :name username})]
    (swap! ROOMS #(-> %
                      (update-in [roomid :users] disj username)
                      (update-in [roomid :events] conj event)
                      (update-in [roomid :eventid] (constantly (:eventid event)))))
    (stub request)))

(defroutes main-routes
  (GET "/" [] (redirect "index.html"))
  (POST "/api/login.cgi" [] login)
  (POST "/api/chat.cgi" [] send-chat-message)
  (GET "/api/geteventssince.cgi" [] get-events-since)
  (POST "/api/vote.cgi" [] stub)
  (POST "/api/updatesentence.cgi" [] stub)
  (POST "/api/ping.cgi" [] stub)
  (POST "/api/part.cgi" [] part)
  (POST "/api/join.cgi" [] join)
  (GET "/api/getstate.cgi" [] getstate)
  (GET "/api/getroomlist.cgi" [] getroomlist)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
