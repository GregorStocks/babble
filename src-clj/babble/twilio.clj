(ns babble.twilio
  (import com.twilio.sdk.TwilioRestClient
          org.apache.http.NameValuePair
          org.apache.http.message.BasicNameValuePair))

(def cred (read-string (slurp (io/resource "s3.edn"))))
(def client (TwilioRestClient. (:twilio-sid cred) (:twilio-token cred)))
(def call-factory (-> client .getAccount .getCallFactory))

(defn fart [request]
  (let [phone-number (-> request :params :number)
        _ (log/warn "NUMBER" phone-number)
        params [(BasicNameValuePair. "Url" "http://babble.cx/fart.xml")
                (BasicNameValuePair. "To" "6072203194")
                (BasicNameValuePair. "From" "6072755129")]
        call (.create call-factory params)]
    (log/warn "CALLED" (.getSid call))))
