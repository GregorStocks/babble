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

(def WORDS (edn/read-string (slurp (io/resource "dictionary.edn"))))
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
   :type "game over"})

(defn empty-room [name rid]
  {:name name
   :users #{}
   :sentences {}
   :votes {}
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

(defn add-event ([rid event] (add-event rid event false))
  ([rid event important?]
     (log/info "Event:" event)
     (swap! ROOMS #(-> (if important?
                         (update-in % [rid :event] (constantly event))
                         %)
                       (update-in [rid :events] conj (new-event event))
                       (update-in [rid :eventid] (constantly (:eventid event)))))))

(defn add-user [rid username]
  (swap! ROOMS #(-> %
                    (update-in [rid :users] conj username)
                    (update-in [rid :scores username] (fn [score] (or score 0))))))

(defn remove-user [rid username]
  (swap! ROOMS update-in [rid :users] disj username))

(defn set-sentence [rid username words]
  (log/info rid username words)
  (swap! ROOMS update-in [rid :sentences username] (constantly words)))

(defn set-vote [rid username votee]
  (log/info rid username votee)
  (swap! ROOMS update-in [rid :votes username] (constantly votee)))

(defn next-round [rid]
  (swap! ROOMS #(-> %
                    (update-in [rid :sentences] (constantly {}))
                    (update-in [rid :votes] (constantly {})))))

(defn round-points! [rid]
  ;; this isn't thread-safe but it's okay because we've only got one thread per room
  (let [votes (:votes (@ROOMS rid))
        votes-by-username (frequencies (vals votes))
        winner (if (seq votes) (apply max-key votes-by-username (keys votes-by-username)))
        points-by-username (merge-with + votes-by-username (if winner {winner 2}))]
    (log/info votes-by-username winner points-by-username)
    (doseq [username (keys points-by-username)]
      (swap! ROOMS update-in [rid :points username] #(+ (or % 0) (or (points-by-username username) 0))))
    (apply merge (map #(hash-map % {:votes (or (votes-by-username %) 0)
                                    :points (or (points-by-username %) 0)
                                    :iswinner (= % winner)
                                    :sentence (or (((@ROOMS rid) :sentences) %)
                                                  [])})
                      (keys votes)))))
