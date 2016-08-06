(ns refiner.input
  (:refer-clojure :exclude [read])
  (:import org.jline.terminal.TerminalBuilder)
  (:import org.jline.reader.LineReaderBuilder))

(def terminal (TerminalBuilder/terminal))

(def reader (let [builder (doto (LineReaderBuilder/builder)
                            (.terminal terminal)
                            (.appName "receipt-refiner"))]
              (.build builder)))

(defn read
  ([prompt] (read prompt nil))
  ([prompt buffer] (.readLine reader prompt nil buffer)))
