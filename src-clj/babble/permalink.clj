(ns babble.permalink
  (import java.util.UUID)
  (require [aws.sdk.s3 :as s3]
           [clj-time.core :as time]
           [clojure.tools.logging :as log]
           [cheshire.core :as json]
           [clojure.walk :as walk]
           ))

(def cred {:access-key "AKIAIRXN6F34BDI6D25Q"
           :secret-key "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"})
(def bucket "babble-permalinks")

(defn serialize [words sentences]
  (json/generate-string {:words words
                         :sentences sentences}))
(defn deserialize [s]
  (walk/keywordize-keys (json/parse-string s)))

(defn archive-room [{:keys [sentences] :as room} words]
  (when (seq sentences)
    (let [key (str (java.util.UUID/randomUUID))]
      (s3/put-object cred bucket key (serialize words sentences))
      (s3/update-object-acl cred bucket key (s3/grant :all-users :read))
      key)))

(defn fetch-room [key]
  (let [result (deserialize (slurp (:content (s3/get-object cred bucket key))))]
    result))
