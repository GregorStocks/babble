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
            [hiccup.util :as hiccup]
            [clojure.set :as set]))

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
   :scores {}
   :autojoin true})

(defonce USERS (atom []))
(defonce ROOMS (atom {69 (empty-room "Poop room" 69)}))

(defn ->long [x]
  (Long. (str x)))

(defn new-lengthless-event
  ([event] (new-lengthless-event event (time/now)))
  ([event now]
   (assoc event :eventid (.getMillis now))))

(defn new-event [length m]
  (let [now (time/now)]
    (merge (new-lengthless-event m now)
           {:end (time/plus now (time/secs length))
            :maxtime length})))

(defn add-event ([rid event] (add-event rid event false))
  ([rid event important?]
   (let [ev (new-lengthless-event event)]
     (swap! ROOMS #(-> (if important?
                         (update-in % [rid :event] (constantly ev))
                         %)
                       (update-in [rid :events] conj ev)
                       (update-in [rid :eventid] (constantly (:eventid ev))))))))

(defn add-user [rid username]
  (swap! ROOMS #(-> %
                    (update-in [rid :users] conj username)
                    (update-in [rid :scores username] (fn [score] (or score 0))))))

(defn remove-user [rid username]
  (swap! ROOMS update-in [rid :users] disj username))

(defn set-sentence [rid username words]
  (swap! ROOMS update-in [rid :sentences] (if (seq words)
                                            #(assoc % username words)
                                            #(dissoc % username))))

(defn set-vote [rid username votee]
  (swap! ROOMS update-in [rid :votes username] (constantly votee)))

(defn ai-sentences []
  ;; todo, this is harder than i expected
  {})

(defn next-round [rid]
  (swap! ROOMS #(update-in % [rid] assoc :sentences (ai-sentences) :votes {})))

(defn next-game [rid]
  (swap! ROOMS #(update-in [rid] assoc :scores {})))

(defn round-points! [rid]
  ;; this isn't thread-safe but it's okay because we've only got one thread per room
  (let [votes (:votes (@ROOMS rid))
        raw-votes-by-username (frequencies (vals votes))
        valid-votes-by-username (select-keys raw-votes-by-username (keys votes))
        tiebroken-votes-by-username (apply merge {:Nobody 0.1}

                                           (map #(hash-map % (* (valid-votes-by-username %)
                                                                (+ 1
                                                                   (* 0.0000001 (reduce + (map count (((@ROOMS rid) :sentences) %)))) ;; break ties by sentence length
                                                                   (* 0.00000001 (rand))))) ;; ensure no exact ties (barring ridiculousness), so it's not based on something users control
                                                (keys valid-votes-by-username)))
        winner (first (apply max-key second (seq tiebroken-votes-by-username)))
        points-by-username (apply merge-with + valid-votes-by-username
                                  (if (votes winner) {winner 2})
                                  (map #(if (= winner (votes %)) {% 1})
                                       (keys votes)))
        raw-votes-by-username-with-nobody-maybe (merge raw-votes-by-username (if (= winner :Nobody) {winner (max 0 (- (count (set/union (set (:users (@ROOMS rid)))
                                                                                                                                        (keys votes)))
                                                                                                                      (count votes)))}))]
    (doseq [username (keys points-by-username)]
      (swap! ROOMS update-in [rid :scores username] #(+ (or % 0) (or (points-by-username username) 0))))
    (apply sorted-map-by
           #(or (= winner %1)
                (and (not= winner %2)
                     (> (get tiebroken-votes-by-username %1 0)
                        (get tiebroken-votes-by-username %2 0))))
           (apply concat (map (fn [username]
                                [username {:votes (or (raw-votes-by-username-with-nobody-maybe username) 0)
                                           :points (or (points-by-username username) 0)
                                           :iswinner (= winner username)
                                           :sentence (or (((@ROOMS rid) :sentences) username)
                                                         [])}])
                              (concat ((@ROOMS rid) :users)
                                      [winner]
                                      (keys points-by-username)
                                      (keys valid-votes-by-username)
                                      (keys votes)))))))

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
  ;; drop all but the 50 most recent events
  (swap! ROOMS update-in [rid :events] (partial take 50)))
