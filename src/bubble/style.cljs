(ns bubble.style
  (:require
    [jc776.react :refer [defnc]]
    [jc776.pp :refer [pp-str]]
    [helix.core :refer [$]]
    [helix.hooks :as hook]
    [helix.dom :as d]))

(defn css-translate [[x y]]
  (str "translate(" x "px, " y "px)"))

(defnc Icon [{:keys [icon]}]
  (d/i {:class ["fa" (str "fa-" icon)]}))

(defnc Link [{:keys [onClick children]}]
  (if onClick
    (d/a {:href "#" :onClick onClick} children)
    children))

(defnc MenuList [{:keys [children]}]
  (d/div {:class "menu"}
    (d/ul {:class "container"}
      children)))
