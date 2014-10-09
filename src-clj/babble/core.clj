(ns babble.core
  (use [babble.model :only (debug?)])
  (require [clojure.tools.logging :as log]
           [babble.model :as model]
           [clj-logging-config.log4j :as logconf]
           [babble.permalink :as permalink]
           [clj-time.core :as time]))

(def goal-score (if debug? 5 30))

;; seconds
(def sentence-making-time (if debug? 10 70))
(def sentence-collecting-time 2)
(def voting-time (if debug? 5 25))
(def vote-collecting-time 2)
(def winner-gloating-time (if debug? 5 10))
(def game-over-time (if debug? 5 20))
(def ping-timeout (time/seconds 15))

(defn housekeeping [rid]
  (model/trim-room rid)
  (when-not debug?
    (doseq [username (:users (@model/ROOMS rid))]
      (when-not (time/before? (time/minus (time/now) ping-timeout)
                              (or ((:last-ping (@model/ROOMS rid)) username)
                                  (time/date-time 2010)))
        (log/info "Removing inactive user" username rid)
        (let [event (model/new-lengthless-event {:type "part"
                                                 :name username})]
          (model/add-event rid event)
          (model/remove-user rid username))))))

(defn wait [secs rid]
  (dotimes [i secs]
    (housekeeping rid)
    (Thread/sleep 1000)))

(defn tick [rid]
  (log/debug "new round" rid)
  (let [words (model/get-word-list)]
    (model/add-event rid
                     (model/new-event sentence-making-time
                                      {:type "new round"
                                       :words words})
                     true)
    (wait sentence-making-time rid)

    (log/debug "collecting" rid)
    (model/add-event rid
                     (model/new-event sentence-collecting-time {:type "collecting"})
                     true)
    (wait sentence-collecting-time rid)

    (log/debug "voting" rid)
    (model/add-event rid
                     (model/new-event voting-time {:type "vote"
                                                   :sentences (:sentences (@model/ROOMS rid))})
                     true)
    (wait voting-time rid)

    (log/debug "voting over" rid)
    (model/add-event rid
                     (model/new-event vote-collecting-time
                                      {:type "voting over"})
                     true)
    (wait vote-collecting-time rid)

    (log/debug "showing winners")
    (when-let [key (permalink/archive-room (@model/ROOMS rid) words)]
      (model/add-event rid {:type "summary"
                            :url (str "/round-" key)}))
    (let [points (model/round-points! rid)]
      (log/info "round points" points)
      (model/add-event rid
                       (model/new-event winner-gloating-time
                                        {:type "winner"
                                         :data points})
                       true))
    (wait winner-gloating-time rid)

    (let [best-score (apply max 0 (vals (:scores (@model/ROOMS rid))))]
      (if (>= best-score goal-score)
        (do
          (model/add-event rid
                           (model/new-event game-over-time
                                            {:type "game over"})
                           true)
          (model/next-game rid)
          (wait game-over-time rid))
        (do
          (model/next-round rid))))))

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
  (logconf/set-loggers! :root
                        {:level :debug
                         :out (doto (org.apache.log4j.RollingFileAppender.
                                     (org.apache.log4j.EnhancedPatternLayout.
                                      "%d{yyyy-MM-dd HH:mm:ssZ}{America/Los_Angeles} %-5p %c: %m%n")
                                     "babble.log"
                                     true)
                                (.setMaxBackupIndex 10))})
  (reset! model/ROOMS {69 (model/empty-room "Poop room" 69)})
  (doseq [rid (keys @model/ROOMS)]
    (.start (Thread. (partial work-loop rid)))))
