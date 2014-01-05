(ns babble.core
  (require [clojure.tools.logging :as log]
           [babble.model :as model]
           [clj-time.core :as time]))

(def SENTENCE-MAKING-TIME 30000)
(def SENTENCE-COLLECTING-TIME 2000)
(def VOTING-TIME 15000)
(def VOTE-COLLECTING-TIME 2000)
(def WINNER-GLOATING-TIME 3000)
(def GAME-OVER-TIME 3000)

(defn tick [rid]
  (log/debug "new round" rid)
  (model/add-event rid
                   (model/new-event {:type "new round"
                                     :timeleft (/ SENTENCE-MAKING-TIME 1000)
                                     :words (model/get-word-list)})
                   true)
  (Thread/sleep SENTENCE-MAKING-TIME)

  (log/debug "collecting" rid)
  (model/add-event rid
                   (model/new-event {:type "collecting"})
                   true)
  (Thread/sleep SENTENCE-COLLECTING-TIME)

  (log/debug "voting" rid)
  (model/add-event rid
                   (model/new-event {:type "vote"
                                     :sentences (:sentences (@model/ROOMS rid))})
                   true)
  (Thread/sleep VOTING-TIME)

  (log/debug "voting over" rid)
  (model/add-event rid
                   (model/new-event {:type "voting over"})
                   true)
  (Thread/sleep VOTE-COLLECTING-TIME)

  (log/debug "showing winners")
  (model/add-event rid
                   (model/new-event {:type "winner"
                                     :data (model/round-points! rid)})
                   true)
  (Thread/sleep WINNER-GLOATING-TIME)
  (model/next-round rid)

  (when false
    (log/debug "game over" rid)
    (model/add-event rid
                     (model/new-event {:type "game over"})
                     true)
    (Thread/sleep GAME-OVER-TIME))

  (doseq [rid (keys @model/ROOMS)]
    (doseq [username (:users (@model/ROOMS rid))]
      (log/info "checking for" username (:last-ping (@model/ROOMS rid))
                (time/before? (time/minus (time/now) (time/minutes 1))
                              (or ((:last-ping (@model/ROOMS rid)) username)
                                  (time/date-time 2010)))
                (or ((:last-ping (@model/ROOMS rid)) username)
                    (time/date-time 2010)))
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
