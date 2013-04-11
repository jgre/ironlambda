(defproject ironlambda "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [overtone "LATEST"]]
  :profiles {:dev {:dependencies [[expectations "1.4.22"]]}}
  :plugins [[lein-expectations "0.0.8"]
            [lein-autoexpect "0.1.2"]])
