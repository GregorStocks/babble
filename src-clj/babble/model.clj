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
            [clojure.java.io :as io]
            [hiccup.util :as hiccup]))

(def WORDS (edn/read-string (slurp (io/resource "dictionary.edn"))))
(defn rand-words [t x]
  (take x (shuffle (t WORDS))))

(def mandatory (:mandatory WORDS))
(def NINETEEN 19)

(defn get-word-list []
  (concat (rand-words :nouns NINETEEN)
          (rand-words :verbs NINETEEN)
          (rand-words :modifiers NINETEEN)
          mandatory
          (rand-words :leftovers NINETEEN)))

(defn initial-event [rid]
  {:eventid 1
   :room rid
   :type "game over"})

(defn empty-room [name rid]
  {:name name
   :users #{}
   :sentences {}
   :last-ping {}
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
  (swap! ROOMS update-in [rid :sentences username] (constantly words)))

(defn set-vote [rid username votee]
  (swap! ROOMS update-in [rid :votes username] (constantly votee)))

(defn next-round [rid]
  (swap! ROOMS #(-> %
                    (update-in [rid :sentences] (constantly { ;;"a ghost" ["All of these sentences are bad."]
                                                             }))
                    (update-in [rid :votes] (constantly {})))))

(defn next-game [rid]
  (swap! ROOMS #(-> %
                    (update-in [rid :scores] (constantly {})))))

(defn round-points! [rid]
  ;; this isn't thread-safe but it's okay because we've only got one thread per room
  (let [votes (:votes (@ROOMS rid))
        raw-votes-by-username (frequencies (vals votes))
        valid-votes-by-username (select-keys raw-votes-by-username (keys votes))
        tiebroken-votes-by-username (apply hash-map (flatten (map #(vector % (+ (valid-votes-by-username %)
                                                                                (* 0.001 (reduce + (map count (((@ROOMS rid) :sentences) %))))))
                                                                  (keys valid-votes-by-username))))
        winner (first (apply max-key second ["Nobody" 0] tiebroken-votes-by-username))
        points-by-username (apply merge-with + valid-votes-by-username
                                  (if (and winner (votes winner)) {winner 2})
                                  (map #(if (= winner (votes %)) {% 1})
                                       (keys votes)))]
    (doseq [username (keys points-by-username)]
      (swap! ROOMS update-in [rid :scores username] #(+ (or % 0) (or (points-by-username username) 0))))
    (apply merge (map #(hash-map % {:votes (or (raw-votes-by-username %) 0)
                                    :points (or (points-by-username %) 0)
                                    :iswinner (= % winner)
                                    :sentence (or (((@ROOMS rid) :sentences) %)
                                                  [])})
                      (concat ((@ROOMS rid) :users)
                              [winner]
                              (keys points-by-username)
                              (keys valid-votes-by-username)
                              (keys votes))))))

(defn ping-user [username rid]
  (swap! ROOMS update-in [rid :last-ping username] (fn [&args] (time/now))))

(defn postprocess-event [event]
  (let [now (time/now)
        end (:end event)
        ev (dissoc event :end)]
    (if end
      (assoc ev :timeleft (if (time/before? now end)
                            (time/in-seconds (time/interval now end))
                            0))
      ev)))

(defn trim-room [rid]
  ;; I'll uncomment this when it's actually necessary.
;;  (swap! ROOMS update-in [rid :events] (partial take-last 50))
  )
