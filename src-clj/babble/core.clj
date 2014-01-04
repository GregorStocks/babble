(ns babble.core
  (require [clojure.tools.logging :as log]))

(defn tick []
  (log/info "tick")
  (Thread/sleep 10000)
  )

(defn work-loop []
  (while true (tick)))

(defn init-background-workers []
  (.start (Thread. work-loop)))
