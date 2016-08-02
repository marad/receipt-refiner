(set-env!
 :source-paths #{"src/clj"}
 :resource-paths #{}
 :dependencies '[[org.clojure/clojure "1.8.0"]])

(task-options!
 pom {:project 'receipt-refiner
      :version "0.0.1-alpha"}
 aot {:namespace '#{refiner.core}}
 jar {:main 'refiner.core})

(deftask build []
  (comp (javac)
        (aot)
        (pom)
        (uber)
        (jar)
        (target)))
