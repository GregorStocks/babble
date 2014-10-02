(ns babble.permalink
  (import java.util.UUID)
  (require [aws.sdk.s3 :as s3]
           [clj-time.core :as time]
           [clojure.tools.logging :as log]
           [cheshire.core :as json]
           [clojure.walk :as walk]
           [clojure.java.io :as io]))

(def cred (read-string (slurp (io/resource "s3.edn"))))
(def bucket "babble-permalinks")

(defn serialize [words summary]
  (json/generate-string {:words words
                         :summary summary}))
(defn deserialize [s]
  (walk/keywordize-keys (json/parse-string s)))

(defn archive-room [{:keys [sentences votes scores] :as room :or {scores {}}} words]
  (when (seq sentences)
    (let [key (str (java.util.UUID/randomUUID))
          output-map (into {} (map (fn [username]
                                     {username {:sentence (sentences username)
                                                :votes (count (votes username))
                                                :points (or (scores username) 0)}})
                                   (keys sentences)))]
      (s3/put-object cred bucket key (serialize words output-map))
      (s3/update-object-acl cred bucket key (s3/grant :all-users :read))
      key)))

(defn fetch-room [key]
  (let [result (deserialize (slurp (:content (s3/get-object cred bucket key))))]
    result))
