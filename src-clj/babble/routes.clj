(ns babble.routes
  (:use compojure.core
        babble.views
        [ring.util.response :only (redirect)]
        [cheshire.core :only (generate-string)]
        [clojure.contrib.generic.functor :only (fmap)]
        [hiccup.middleware :only (wrap-base-url)]
        [babble.model])
  (:require [compojure.route :as route]
            [clojure.tools.logging :as log]
            [compojure.handler :as handler]
            [clj-time.core :as time]
            [compojure.response :as response]
            [babble.dictionary :as dictionary]))

(defn response [m]
      {:body (generate-string m)})

(defn stub [request]
  (response {"status" "OK"}))

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
    (add-event roomid {:type "chat"
                       :username username
                       :text text}))
  (stub request))

(defn get-events-since [request]
  (let [eventid (try (->long ((:query-params request) "eventid"))
                     (catch Exception e (log/info request)
                            0))
        roomid (->long ((:query-params request) "roomid"))]
    (response {"status" "OK"
               "events" (take-last 50 (map postprocess-event (filter #(> (:eventid %) eventid)
                                                                     (:events (@ROOMS roomid)))))})))

(defn getstate [request]
  (let [roomid (->long ((:query-params request) "roomid"))]
    (response {"status" "OK"
               "state" (update-in (select-keys (@ROOMS roomid) [:users :scores :event :eventid])
                                  [:event]
                                  postprocess-event)})))

(defn getroomlist [request]
  (response {"status" "OK"
             "rooms" (fmap #(select-keys % [:name :users]) @ROOMS)}))

(defn join [request]
  (let [params (:form-params request)
        username (params "sesskey")
        roomid (->long (params "roomid"))
        event (new-event {:type "join"
                          :score 0
                          :name username})]
    (ping-user username roomid)
    (when-not (contains? (:users (@ROOMS roomid)) username) ;; legitimately new joiner
      (add-event roomid event)
      (add-user roomid username))
    (stub request)))

(defn part [request]
  (let [params (:form-params request)
        username (params "sesskey")
        roomid (->long (params "roomid"))
        event (new-event {:type "part"
                          :name username})]
    (add-event roomid event)
    (remove-user roomid username)
    (stub request)))

(defn update-sentence [request]
  (let [params (:form-params request)
        username (params "sesskey")
        roomid (->long (params "roomid"))
        words (flatten [(params "words")])]
    (set-sentence roomid username words)))

(defn vote [request]
  (let [params (:form-params request)
        username (params "sesskey")
        roomid (->long (params "roomid"))
        votee (params "sentenceid")]
    (set-vote roomid username votee)))

(defroutes main-routes
  (GET "/" [] (redirect "index.html"))
  (POST "/api/login.cgi" [] login)
  (POST "/api/chat.cgi" [] send-chat-message)
  (GET "/api/geteventssince.cgi" [] get-events-since)
  (POST "/api/vote.cgi" [] vote)
  (POST "/api/updatesentence.cgi" [] update-sentence)
  (POST "/api/ping.cgi" [] join)
  (POST "/api/part.cgi" [] part)
  (POST "/api/join.cgi" [] join)
  (GET "/api/getstate.cgi" [] getstate)
  (GET "/api/getroomlist.cgi" [] getroomlist)
  (GET "/js/dictionary-autogen.js" [] (fn [request] hash-map :body (dictionary/dict-js)))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
