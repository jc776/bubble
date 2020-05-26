(ns bootstrap.nav
  (:require
    [jc776.react :refer [defnc]]
    [jc776.pp :refer [pp-str]]
    [helix.core :refer [$]]
    [helix.hooks :as hook]
    [helix.dom :as d]))

(defn seek [pred coll]
  (first (filter pred coll)))

(defnc TabView [{:keys [items]}]
  (let [[selected setSelected] (hook/use-state nil)
        active-item
        (or (seek #(= selected (:key %)) items)
            (first items))
        active-key (:key active-item)]
    (d/div
      (d/ul {:class "nav nav-tabs"}
        (for [{:keys [key tab]} items]
          (d/li {:class "nav-item" :key (str key)}
            (d/a {:class ["nav-link" (when (= active-key key) "active")]
                  :href "#"
                  :onClick #(setSelected key)}
              tab))))
      (:content active-item))))

(comment
  ($ nav/TabView
    {:items [{:key 1 :tab "First" :content "first"}
             {:key 2 :tab "Second" :content "second"}
             {:key 3 :tab "Third" :content (d/h1 "OK")}]}))
