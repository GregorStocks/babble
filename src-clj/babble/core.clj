(ns babble.core
  (require [clojure.tools.logging :as log]
           [babble.model :as model]
           [clj-time.core :as time]))

(def GOAL-SCORE 2)
(def SENTENCE-MAKING-TIME 15000)
(def SENTENCE-COLLECTING-TIME 2000)
(def VOTING-TIME 15000)
(def VOTE-COLLECTING-TIME 2000)
(def WINNER-GLOATING-TIME 7000)
(def GAME-OVER-TIME 15000)
(def PING-TIMEOUT (time/seconds 15))

(defn end-time [delta]
  (time/plus (time/now) (time/millis delta)))

(defn housekeeping [rid]
  (model/trim-room rid)
  (doseq [username (:users (@model/ROOMS rid))]
    (when-not (time/before? (time/minus (time/now) PING-TIMEOUT)
                            (or ((:last-ping (@model/ROOMS rid)) username)
                                (time/date-time 2010)))
      (log/info "Removing inactive user" username rid)
      (let [event (model/new-event {:type "part"
                                    :name username})]
        (model/add-event rid event)
        (model/remove-user rid username)))))

(defn wait [msecs rid]
  (dotimes [i (/ msecs 1000)]
    (housekeeping rid)
    (Thread/sleep 1000)))

(defn tick [rid]
  (log/debug "new round" rid)
  (model/add-event rid
                   (model/new-event {:type "new round"
                                     :end (end-time SENTENCE-MAKING-TIME)
                                     :words (model/get-word-list)})
                   true)
  (wait SENTENCE-MAKING-TIME rid)

  (log/debug "collecting" rid)
  (model/add-event rid
                   (model/new-event {:type "collecting"
                                     :end (end-time SENTENCE-COLLECTING-TIME)})
                   true)
  (wait SENTENCE-COLLECTING-TIME rid)

  (log/debug "voting" rid)
  (model/add-event rid
                   (model/new-event {:type "vote"
                                     :sentences (:sentences (@model/ROOMS rid))
                                     :end (end-time VOTING-TIME)})
                   true)
  (wait VOTING-TIME rid)

  (log/debug "voting over" rid)
  (model/add-event rid
                   (model/new-event {:type "voting over"
                                     :end (end-time VOTE-COLLECTING-TIME)})
                   true)
  (wait VOTE-COLLECTING-TIME rid)

  (log/debug "showing winners")
  (model/add-event rid
                   (model/new-event {:type "winner"
                                     :data (model/round-points! rid)
                                     :end (end-time WINNER-GLOATING-TIME)})
                   true)
  (wait WINNER-GLOATING-TIME rid)
  (model/next-round rid)

  (let [best-score (apply max 0 (vals (:scores (@model/ROOMS rid))))]
    (when (>= best-score GOAL-SCORE)
      (model/add-event rid
                       (model/new-event {:type "game over"
                                         :end (end-time GAME-OVER-TIME)})
                       true)
      (model/next-game rid))
    (wait GAME-OVER-TIME rid)))

(defn work-loop [rid]
  (while true
    (try
      (tick rid)
      (catch Exception e
        (log/error e)
        (.printStackTrace e)
        ;; good enough, keep going
        ))))

(defn init-background-workers []
  (doseq [rid (keys @model/ROOMS)]
     (.start (Thread. (partial work-loop rid)))))
