(ns babble.model
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
            [compojure.response :as response]
            [clojure.tools.reader.edn :as edn]
            [clojure.java.io :as io]))

(defonce WORDS (edn/read-string (slurp (io/resource "dictionary.edn"))))
(defn rand-word [t]
  (rand-nth (t WORDS)))

(def mandatory (:mandatory WORDS))

(defn get-word-list []
  (concat (repeatedly 40 #(rand-word :nouns))
          (repeatedly 40 #(rand-word :verbs))
          (repeatedly 40 #(rand-word :modifiers))
          mandatory
          (repeatedly (- 40 (count mandatory))
                  #(rand-word :leftovers))))

(defn initial-event [rid]
  {:eventid 1
   :room rid
   :type "new round"
   :timeleft 0
   :words (get-word-list)})

(defn empty-room [name rid]
  {:name name
   :users #{}
   :events [(initial-event rid)]
   :event (initial-event rid)
   :eventid 1
   :scores {}})

(defonce USERS (atom []))
(defonce ROOMS (atom {69 (empty-room "Poop room" 69)}))

(defn ->long [x]
  (Long. (str x)))

(defn new-event [m]
  (merge m {:eventid (.getMillis (time/now))}))

(defn add-event [rid event]
  (log/info "Event:" event)
  (swap! ROOMS #(-> %
                    (update-in [rid :events] conj (new-event event))
                    (update-in [rid :eventid] (constantly (:eventid event))))))

(defn add-user [rid username]
  (swap! ROOMS #(-> %
                    (update-in [rid :users] conj username)
                    (update-in [rid :scores username] (fn [score] (or score 0))))))

(defn remove-user [rid username]
  (swap! ROOMS update-in [rid :users] disj username))
