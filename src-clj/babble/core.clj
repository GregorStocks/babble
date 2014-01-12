(ns babble.core
  (require [clojure.tools.logging :as log]
           [babble.model :as model]
           [clj-time.core :as time]))

(def GOAL-SCORE 30)

;; seconds
(def SENTENCE-MAKING-TIME 70)
(def SENTENCE-COLLECTING-TIME 2)
(def VOTING-TIME 25)
(def VOTE-COLLECTING-TIME 2)
(def WINNER-GLOATING-TIME 10)
(def GAME-OVER-TIME 20)
(def PING-TIMEOUT (time/seconds 15))

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

(defn wait [secs rid]
  (dotimes [i secs]
    (housekeeping rid)
    (Thread/sleep 1000)))

(defn tick [rid]
  (log/debug "new round" rid)
  (model/add-event rid
                   (model/new-event SENTENCE-MAKING-TIME
                                    {:type "new round"
                                     :words (model/get-word-list)})
                   true)
  (wait SENTENCE-MAKING-TIME rid)

  (log/debug "collecting" rid)
  (model/add-event rid
                   (model/new-event SENTENCE-COLLECTING-TIME {:type "collecting"})
                   true)
  (wait SENTENCE-COLLECTING-TIME rid)

  (log/debug "voting" rid)
  (model/add-event rid
                   (model/new-event VOTING-TIME {:type "vote"
                                                 :sentences (:sentences (@model/ROOMS rid))})
                   true)
  (wait VOTING-TIME rid)

  (log/debug "voting over" rid)
  (model/add-event rid
                   (model/new-event VOTE-COLLECTING-TIME
                                    {:type "voting over"})
                   true)
  (wait VOTE-COLLECTING-TIME rid)

  (log/debug "showing winners")
  (model/add-event rid
                   (model/new-event WINNER-GLOATING-TIME
                                    {:type "winner"
                                     :data (model/round-points! rid)})
                   true)
  (wait WINNER-GLOATING-TIME rid)
  (model/next-round rid)

  (let [best-score (apply max 0 (vals (:scores (@model/ROOMS rid))))]
    (when (>= best-score GOAL-SCORE)
      (model/add-event rid
                       (model/new-event GAME-OVER-TIME
                                        {:type "game over"})
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
