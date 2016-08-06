(ns refiner.printer
  (:require [clojure.string :as s]))

(defn show-entry [entry]
  (format "%-25s%8s%8s%8s"
          (:name entry)
          (:amount entry)
          (:item-price entry)
          (:price entry)))

(defn show-list [list]
   (->> list
        (map :parsed)
        (map show-entry)
        (doall)))

(defn print-entry [entry]
  (println (show-entry entry)))

(defn print-list [list]
  (println (s/join "\n" (show-list list))))

(defn print-enumerated-list [list]
  (println (s/join "\n" (map-indexed #(format "%3d. %s" %1 %2)
                                     (show-list list)))))
