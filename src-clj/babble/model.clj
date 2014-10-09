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

(def debug? false)
(def WORDS (edn/read-string (slurp (io/resource "dictionary.edn"))))
(defn rand-words [t x]
  (take x (shuffle (t WORDS))))

(def mandatory (:mandatory WORDS))
(def NINETEEN 19)

(defn get-word-list []
  (log/debug "WORD LIST GETTING")
  (concat (rand-words :nouns NINETEEN)
          (rand-words :verbs NINETEEN)
          (rand-words :modifiers NINETEEN)
          mandatory
          (rand-words :leftovers NINETEEN)))

(defn initial-event [rid]
  {:eventid 1
   :room rid
   :type "game over"})

(defn ai-sentences []
  (log/debug "AI SENTENCES")
  ;; todo, this is harder than i expected
  (if debug?
    {"ghost" ["ass" "butt"]}
    {}))

(defn empty-room [name rid]
  {:name name
   :users (if debug? #{"ghost"} #{})
   :sentences (ai-sentences)
   :last-ping {}
   :votes (if debug? {"ghost" "ghost"} {})
   :events [(initial-event rid)]
   :event (initial-event rid)
   :eventid 1
   :scores {}
   :autojoin true})

(defonce USERS (atom []))
(defonce ROOMS (atom nil))

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

(defn next-round [rid]
  (swap! ROOMS #(update-in % [rid] assoc
                           :sentences (ai-sentences)
                           :votes (if debug? {"ghost" "ghost"} {}))))

(defn next-game [rid]
  (swap! ROOMS #(update-in % [rid] assoc :scores {})))

(defn map-filter [f m]
  (into {} (filter f m)))

;; this isn't exactly thread-safe but it's okay because we've only got one thread per room
(defn round-points! [rid]
  ;; ignore votes from folks who didn't make a sentence
  (let [room (@ROOMS rid)
        sentences (:sentences room)
        votes (:votes room)
        vote-count #(count ((frequencies votes) %1))
        score (fn [username]
                [(if (sentences username) 1 0) ;; whether they submitted a sentence
                 (if (votes username) 1 0)     ;; whether they voted
                 (vote-count username)         ;; votes received
                 (count (sentences username))  ;; sentence length
                 (rand)])
        winner (last (sort-by score (keys votes)))
        points #(+ (if (= winner %) 2 0)
                   (if (votes %) (vote-count %) 0)
                   (if (and winner (= winner (votes %))) 1 0))]
    (log/info "users are" (:users room))
    (doseq [username (:users room)]
      (log/info username "POINTS" (points username) "VOTE" (votes username) "vote count" (vote-count username))
      (swap! ROOMS update-in [rid :scores username] #(+ (or % 0) (points username))))
    (apply sorted-map-by
           #(compare (score %1) (score %2))
           (mapcat (fn [[username sentence]]
                     [username {:votes (vote-count username)
                                :points (points username)
                                :iswinner (= winner username)
                                :sentence sentence}])
                   sentences))))

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
