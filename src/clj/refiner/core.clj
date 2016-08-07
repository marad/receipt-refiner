(ns refiner.core
  (:require [clojure.edn :as edn]
            [refiner.input :as in]
            [refiner.printer :as p]
            [refiner.chooser :as ch]
            [mount.core :refer [defstate] :as mount])
  (:gen-class))

(defn normalize-number-string [number-string]
  (if (empty? number-string) "0"
      (-> number-string
          (.replaceAll "O" "0")
          (.replaceAll "," "."))))

(defn normalize-numbers [entries]
  (map #(-> %
            (update-in [:parsed :amount] normalize-number-string)
            (update-in [:parsed :item-price] normalize-number-string)
            (update-in [:parsed :price] normalize-number-string))
       entries))

(defn read-data [file-path]
  (normalize-numbers (into [] (read-string (slurp file-path)))))

(defstate data :start (atom (read-data (first (mount/args)))))

(defn update-item [item key prompt]
  (->> (in/read prompt (key item))
       (assoc item key)))

(defn refine-entry [{:keys [parsed input] :as entry}]
  (println input)
  (let [updated (-> parsed
                    (update-item :name "Name:\t\t")
                    (update-item :amount "Amount:\t\t")
                    (update-item :item-price "Item Price:\t")
                    (update-item :price "Price:\t\t"))]
    (println "----------")
    (assoc entry :parsed updated)))

(defn refine-list [list]
  (let [list (->> list
                  (map refine-entry)
                  (doall))]
    (reset! data list)))

(defn refine-single-entry [list]
  (let [v (into [] list)
        index (ch/choose-entry list)]
    (reset! data
            (update v index refine-entry))))

(defn save-data [out-path data]
  (spit out-path (with-out-str (pr data))))

(defn parse-num [s]
  (if (empty? s) 0
      (read-string (normalize-number-string s))))

(defn calc-sum [entries]
  (->> entries
       (map :price)
       (map parse-num)
       (apply +)
       (format "%.2f")))

(defn save-list []
  (let [path (first (mount/args))]
    (print "Saving to file" (str path "... "))
    (save-data path @data)
    (println "done!")))

(defn delete-entry []
  (let [index (ch/choose-entry @data)]
    (println "Removing item at index" index)
    (swap! data #(->> %
                      (map-indexed vector)
                      (filter (fn [[i _]] (not (= i index))))
                      (map second)))
    nil))

(defn validate-entry-amount [[index {item :parsed}]]
  (let [amount (parse-num (:amount item))
        item-price (parse-num (:item-price item))
        price (parse-num (:price item))
        expected-price (* amount item-price)
        ]
    (if (> (Math/abs (- price expected-price)) 0.01)
      (str "Invalid price at " index " is " price " should be " expected-price)
      "ok")))

(defn validate-amounts []
  (let [errors (->> @data
                    (map-indexed vector)
                    (map validate-entry-amount)
                    (filter #(not (= "ok" %))))]
    (if (empty? errors)
      (println "All well and good!")
      (doall (map println errors)))))

(defn perform-job [job-letter]
  (cond
    (= job-letter "p") (p/print-enumerated-list @data)
    (= job-letter "t") (println "Sum:" (calc-sum (map :parsed @data)))
    (= job-letter "r") (refine-list @data)
    (= job-letter "c") (refine-single-entry @data)
    (= job-letter "s") (save-list)
    (= job-letter "d") (delete-entry)
    (= job-letter "v") (validate-amounts)
    (= job-letter "q") (System/exit 0)))

(defn app-repl []
  (perform-job (ch/menu))
  (println))

(defn -main [& args]
  (mount/start-with-args args)
  (loop []
    (try (app-repl)
         (catch Exception e
           (println (.getMessage e))))
    (recur)))
