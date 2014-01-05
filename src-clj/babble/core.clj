(ns babble.core
  (require [clojure.tools.logging :as log]
           [babble.model :as model]))

(defn each-room [f]
  (doseq [rid (keys @model/ROOMS)]
    (f rid)))

(defn tick []
  (log/info "tick")
  (Thread/sleep 10000)
  (each-room #(model/add-event % (model/new-event {:type "new round"
                                                   :words ["ass" "butt" "dick" "hell" "dildos"]})))

  (Thread/sleep 5000)
  (each-room #(model/add-event % (model/new-event {:type "winner"
                                                   :data {"poopman" {"votes" 0
                                                                     "points" 5
                                                                     "iswinner" true}
                                                          "boobman" {"votes" 6
                                                                     "points" 4
                                                                     "iswinner" false}}}))))

(defn work-loop []
  (while true (tick)))

(defn init-background-workers []
  (.start (Thread. work-loop)))
