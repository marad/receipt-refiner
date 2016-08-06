(require '[refiner.core :as c])
(require '[refiner.chooser :as ch])
(require '[refiner.input :as in])
(require '[refiner.printer :as p])
(require '[mount.core :as mount])

(mount/stop)
(mount/start-with-args ["data/intermarche-2016-07-17.edn"])
(mount/start-with-args ["data/test.edn"])

