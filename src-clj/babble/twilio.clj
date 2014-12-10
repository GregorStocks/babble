(ns babble.twilio
  (require [clojure.tools.logging :as log]
           [clojure.java.io :as io])
  (import com.twilio.sdk.TwilioRestClient
          org.apache.http.NameValuePair
          org.apache.http.message.BasicNameValuePair))

(defn fart [request]
  (let [cred (read-string (slurp (io/resource "s3.edn")))
        client (TwilioRestClient. (:twilio-sid cred) (:twilio-token cred))
        call-factory (-> client .getAccount .getCallFactory)
        phone-number (-> request :params :number)
        _ (log/warn "NUMBER" phone-number)
        params [(BasicNameValuePair. "Url" "http://babble.cx/fart.xml")
                (BasicNameValuePair. "To" phone-number)
                (BasicNameValuePair. "From" "6072755129")
                (BasicNameValuePair. "Method" "GET")]
        call (.create call-factory params)]
    (log/warn "CALLED" (.getSid call))
    {:status 200
     :body "neat"}))
