(ns babble.permalink
  (require [aws.sdk.s3 :as s3]
           [clj-time.core :as time]
           [clojure.tools.logging :as log]
           [cheshire.core :as json]))

(def cred {:access-key "AKIAIRXN6F34BDI6D25Q"
           :secret-key "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"})
(def bucket "babble-permalinks")

(defn serialize [words sentences]
  (json/generate-string {:words words
                         :sentences sentences}))
(defn deserialize [s]
  (json/parse-string s))

(defn archive-room [{:keys [sentences] :as room} words]
  (when (seq sentences)
    (let [key (str (.getMillis (time/now)) "-" (rand-int 100000))]
      (log/info "SHIT" sentences)
      (s3/put-object cred bucket key (serialize words sentences))
      (s3/update-object-acl cred bucket key (s3/grant :all-users :read))
      key)))
