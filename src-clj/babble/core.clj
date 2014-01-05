(ns babble.core
  (require [clojure.tools.logging :as log]
           [babble.model :as model]))

(defn tick [rid]
  (log/debug "new round" rid)
  (model/add-event rid (model/new-event {:type "new round"
                                         :timeleft 30
                                         :words (model/get-word-list)}))
  (Thread/sleep 30000)

  (log/debug "collecting" rid)
  (model/add-event rid (model/new-event {:type "collecting"}))
  (Thread/sleep 5000)

  (log/debug "voting" rid)
  (model/add-event rid (model/new-event {:type "vote"
                                         :sentences (:sentences (@model/ROOMS rid))}))
  (Thread/sleep 15000)

  (log/debug "voting over" rid)
  (model/add-event rid (model/new-event {:type "voting over"}))
  (Thread/sleep 2000)

  (log/debug "showing winners")
  (model/add-event rid (model/new-event {:type "winner"
                                         :data (model/round-points! rid)}))
  (Thread/sleep 10000)
  (model/next-round rid)

  (when true
    (log/debug "game over" rid)
    (model/add-event rid (model/new-event {:type "game over"}))
    (Thread/sleep 5000)))

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
