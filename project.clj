(defproject numbers "0.1.0-SNAPSHOT"
  :description "Experimenting with numerical algorithms in java."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]

                 ;; linear algebra

                 [org.nd4j/nd4j-jblas "0.4-rc3.5"]


                 [org.ejml/all "0.28"]

                 [org.jblas/jblas "1.2.4"]
                 ]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}})
