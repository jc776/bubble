(ns bubble.browser
  (:require
    [cljs.env]
    [orchard.cljs.analysis :as cljs-analysis]
    [orchard.cljs.meta :as cljs-meta]
    [orchard.namespace :as ns]
    [orchard.info :as info]
    [jc776.pp :refer [pp-str]]))

(defn ns-list-cljs2 [env]
  (cljs-analysis/all-ns env
       (keys) ; too big...
       (filter some?) ; ???
       (map name)
       (sort)
       (into [])))

(defn cljs-ns-info [env]
  (->> (info/cljs-meta {:dialect :cljs :env env :sym 'bubble.model})
    (keys)
    (map name)
    (into [])))

(defn ns-list-cljs [env]
  `(quote ~(cljs-analysis/all-ns env)))

(defmacro remote-clj-ns-list []
  (->> (ns/project-namespaces)
    (map name)
    (into [])))

(defmacro remote-cljs-ns-list []
  (let [env @cljs.env/*compiler*]
    (ns-list-cljs env)))
