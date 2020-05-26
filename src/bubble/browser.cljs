(ns bubble.browser
  (:require
    [bubble.window :refer [ManagedWindow WindowView]]
    [bubble.model :as model :refer [moveBy]]
    [bubble.style :refer [Link]]
    [bootstrap.nav :as nav]
    [bootstrap.table :as table]
    [jc776.react :refer [defnc]]
    [jc776.pp :refer [pp-str pp-short pp-mid]]
    [helix.core :refer [$]]
    [helix.hooks :as hook]
    [helix.dom :as d])
  (:require-macros [bubble.browser]))

;; Do CLJS inspector
;; - list of functions returning {:title "Blah" :priority 100 :cmp ($ ...)}
;; Then ns-browser is "inspect CLJS's all-ns list"
;; Then do CLJ connection/inspector

(defonce inspector-fns (atom {}))

(defn register-inspector! [key f]
  (swap! inspector-fns assoc key f))

(defnc EdnView [{:keys [data]}]
  (d/div {:class "inspector"}
    (d/pre (pp-mid data))))

(register-inspector! :edn
  (fn [data props]
    {:rank 1000
     :tab "EDN"
     :content ($ EdnView {:data data & props})}))

(defnc MapView [{:keys [data inspect] :as props}]
  (d/div {:class "inspector"}
    ($ table/TableView
      {:cols [["Key" first]
              ["Value" second]]
       :cell-fn (fn [x] ($ Link {:onClick #(inspect x)} (d/pre (pp-short x))))
       :key-fn first
       :data data})))
  ;; on click nav - or just have an <a> link...

(register-inspector! :map
  (fn [data props]
    (when (map? data)
      {:rank 100
       :tab "Map"
       :content ($ MapView {:data data & props})})))

(defnc CollView [{:keys [data inspect] :as props}]
  (d/div {:class "inspector"}
    ($ table/TableView
      {:cols [["Index" first]
              ["Value" second]]
       :cell-fn (fn [x] ($ Link {:onClick #(inspect x)} (d/pre (pp-short x))))
       :key-fn first
       :data (map-indexed vector data)})))
  ;; on click nav - or just have an <a> link...

(register-inspector! :coll
  (fn [data props]
    (when (and (coll? data) (not (map? data)))
      {:rank 101
       :tab "Indexed"
       :content ($ CollView {:data data & props})})))

(defn inspector-tabs [data props]
  (->> (for [[key f] @inspector-fns]
         (if-let [tab (f data props)]
           (assoc tab :key key)))
    (filter some?)
    (sort-by :rank)))

(defnc Inspector [{:keys [data inspect]}]
  ;; ... use-atom inspector-fns
  (let [tabs (inspector-tabs data {:inspect inspect})]
    ($ nav/TabView {:items tabs})))

(defnc NamespaceBrowser []
  (d/div
    (d/h2 "CLJ")
    (d/ul
      (for [str (bubble.browser/remote-clj-ns-list)]
        (d/li str)))
    (d/h2 "CLJS")
    (d/ul
      #_(bubble.browser/remote-cljs-ns-list)
      #_(for [str (bubble.browser/remote-cljs-ns-list)]
          (d/li str)))))

(def sample-data
  (bubble.browser/remote-cljs-ns-list))

(declare InspectorCard)
(defnc InspectorCard [{:keys [data send id drag position]}]
  ($ WindowView
    {:title "Inspector"
     :onClose #(send model/removeCard id)
     :position position :onDragMove #(send model/moveCard id %)
     :drag drag :setDrag #(send model/setCardDrag id %)}
    ($ Inspector
      {:data data
       :inspect (fn [data]
                  (send model/addCard
                    {:type #'InspectorCard
                     :data data
                     :position (moveBy position [200 0])}))})))

(defnc NamespaceBrowserCard [props]
  ($ InspectorCard {:data sample-data & props}))
