(defproject babble "0.0.69"
  :description "A poopy butt"
  :source-paths ["src-clj"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2014" :exclusions [org.apache.ant/ant]]
                 [cheshire "5.0.1"]
                 [compojure "1.1.6"]
                 [clj-time "0.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [hiccup "1.0.4"]]
  :plugins [[lein-cljsbuild "1.0.1"]
            [lein-ring "0.8.10"]]
  :cljsbuild {
    :builds [{:source-paths ["src-cljs"]
              :compiler {:output-to "resources/public/js/babble-autogen.js"
                         :optimizations :whitespace
                         :pretty-print true}}]}
  :ring {:handler babble.routes/app
         :init babble.core/init-background-workers})
