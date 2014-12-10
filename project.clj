(defproject babble "0.9.1"
  :description "Searching for meaning where, ultimately, there is none."
  :source-paths ["src-clj"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2014" :exclusions [org.apache.ant/ant]]
                 [cheshire "5.0.1"]
                 [lein-ring "0.8.12"]
                 [ring "1.2.1"]
                 [compojure "1.1.6"]
                 [clj-time "0.6.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [clj-logging-config "1.9.12"]
                 [hiccup "1.0.4"]
                 [log4j "1.2.16"]
                 [clj-aws-s3 "0.3.10"]
                 [com.twilio.sdk/twilio-java-sdk "3.4.5"]]
  :plugins [[lein-cljsbuild "1.0.1"]
            [lein-ring "0.8.12"]
            [lein-ver "1.0.1"]]
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :cljsbuild {
    :builds [{:source-paths ["cljs-src"]
              :compiler {:output-to "resources/public/js/babble-autogen.js"
                         :optimizations :whitespace
                         :pretty-print true}}]}
  :ring {:handler babble.routes/app
         :init babble.core/init-background-workers})
