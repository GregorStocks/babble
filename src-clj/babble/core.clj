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

(defn end-time [delta]
  (time/plus (time/now) (time/millis delta)))

(defn tick [rid]
  (log/debug "new round" rid)
  (model/add-event rid
                   (model/new-event {:type "new round"
                                     :end (end-time SENTENCE-MAKING-TIME)
                                     :words (model/get-word-list)})
                   true)
  (Thread/sleep SENTENCE-MAKING-TIME)

  (log/debug "collecting" rid)
  (model/add-event rid
                   (model/new-event {:type "collecting"
                                     :end (end-time SENTENCE-COLLECTING-TIME)})
                   true)
  (Thread/sleep SENTENCE-COLLECTING-TIME)

  (log/debug "voting" rid)
  (model/add-event rid
                   (model/new-event {:type "vote"
                                     :sentences (:sentences (@model/ROOMS rid))
                                     :end (end-time VOTING-TIME)})
                   true)
  (Thread/sleep VOTING-TIME)

  (log/debug "voting over" rid)
  (model/add-event rid
                   (model/new-event {:type "voting over"
                                     :end (end-time VOTE-COLLECTING-TIME)})
                   true)
  (Thread/sleep VOTE-COLLECTING-TIME)

  (log/debug "showing winners")
  (model/add-event rid
                   (model/new-event {:type "winner"
                                     :data (model/round-points! rid)
                                     :end (end-time WINNER-GLOATING-TIME)})
                   true)
  (Thread/sleep WINNER-GLOATING-TIME)
  (model/next-round rid)

  (let [best-score (apply max 0 (vals (:scores (@model/ROOMS rid))))]
    (when (>= best-score GOAL-SCORE)
      (model/add-event rid
                       (model/new-event {:type "game over"
                                         :end (end-time GAME-OVER-TIME)})
                       true)
      (model/next-game rid))

    (Thread/sleep GAME-OVER-TIME))
  (model/trim-room rid)

  (doseq [rid (keys @model/ROOMS)]
    (doseq [username (:users (@model/ROOMS rid))]
      (when-not (time/before? (time/minus (time/now) (time/minutes 1))
                              (or ((:last-ping (@model/ROOMS rid)) username)
                                  (time/date-time 2010)))
        (log/info "Removing inactive user" username rid)
        (let [event (model/new-event {:type "part"
                                      :name username})]
          (model/add-event rid event)
          (model/remove-user rid username))))))

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
