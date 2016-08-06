(set-env!
 :source-paths #{"src/clj"}
 :resource-paths #{}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [jline/jline "3.0.0.M1"]
                 [mount "0.1.10"]
                 [org.clojure/tools.cli "0.3.5"]])

(task-options!
 pom {:project 'receipt-refiner
      :version "0.0.1-alpha"}
 aot {:namespace '#{refiner.core}}
 jar {:main 'refiner.core}
 ;repl {:eval "(load-file user.clj)"}
 )

(deftask build []
  (comp (javac)
        (aot)
        (pom)
        (uber)
        (jar)
        (target)))
