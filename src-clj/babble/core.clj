(ns babble.core
  (require [clojure.tools.logging :as log]
           [babble.model :as model]
           [clj-time.core :as time]))

(defn tick [rid]
  (log/debug "new round" rid)
  (model/add-event rid
                   (model/new-event {:type "new round"
                                     :timeleft 30
                                     :words (model/get-word-list)})
                   true)
  (Thread/sleep 30000)

  (log/debug "collecting" rid)
  (model/add-event rid
                   (model/new-event {:type "collecting"})
                   true)
  (Thread/sleep 5000)

  (log/debug "voting" rid)
  (model/add-event rid
                   (model/new-event {:type "vote"
                                     :sentences (:sentences (@model/ROOMS rid))})
                   true)
  (Thread/sleep 15000)

  (log/debug "voting over" rid)
  (model/add-event rid
                   (model/new-event {:type "voting over"})
                   true)
  (Thread/sleep 2000)

  (log/debug "showing winners")
  (model/add-event rid
                   (model/new-event {:type "winner"
                                         :data (model/round-points! rid)})
                   true)
  (Thread/sleep 10000)
  (model/next-round rid)

  (when false
    (log/debug "game over" rid)
    (model/add-event rid
                      (model/new-event {:type "game over"})
                      true)
    (Thread/sleep 5000))

  (doseq [rid (keys @model/ROOMS)]
    (doseq [username (:users (@model/ROOMS rid))]
      (when-not (time/before? (time/minus (time/now) (time/minutes 1))
                              (or ((:last-ping (@model/ROOMS rid)) username) (time/date-time 2010)))
        (log/info "Removing inactive user" username rid)
        (model/remove-user rid username)))))

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
