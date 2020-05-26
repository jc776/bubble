(ns jc776.pp
  (:require [clojure.pprint :as pp]))

(defn pp-str [obj]
  (with-out-str (pp/pprint obj)))

(defn pp-short [obj]
  (binding [*print-length* 2]
    (with-out-str (pp/pprint obj))))

(defn pp-mid [obj]
  (binding [*print-length* 6]
    (with-out-str (pp/pprint obj))))
