(ns refiner.chooser
  (:require [refiner.input :as in]
            [refiner.printer :as p]))

(defn menu []
  (println (str
            "--- Receipt Refiner ---\n\n"
            "p. print list\n"
            "t. print total sum\n"
            "r. refine all entries\n"
            "c. correct single entry\n"
            "d. delete single entry\n"
            "s. save current list\n"
            "v. validate amounts\n"
            "q. quit\n"))
  (let [choice (in/read "> ")]
    (if (#{"p" "t" "r" "c" "d" "s" "v" "q"} choice)
      choice
      (do (println "Invalid option: " choice)
          (recur)))))

(defn choose-entry [list]
  (p/print-enumerated-list list)
  (let [choice (read-string (in/read "> "))]
    (if ((set (range (count list))) choice)
      choice
      (do (println "There is no item with index" choice)
          (recur list)))))
