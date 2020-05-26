(ns jc776.react
  (:require-macros [jc776.react]))
  
(defonce used-hooks (atom {}))

(defn signature-equal? [sym hooks]
  (= (get @used-hooks sym) hooks))