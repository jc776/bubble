(ns ^:figwheel-hooks bubble.dev
  (:require
    [bubble.style :refer [MenuList Icon css-translate]]
    [bubble.model :as model :refer [moveBy]]
    [bubble.window :refer [ManagedWindow WindowView]]
    [bubble.browser :refer [NamespaceBrowserCard]]
    [jc776.react :refer [defnc]]
    [jc776.pp :refer [pp-str]]
    [helix.core :refer [$ <>]]
    [helix.hooks :as hook]
    [helix.dom :as d]
    ["react-dom" :as react-dom]))

;; Viewport - pan/zoom "infinite canvas"

(defn isCurrentTarget [ev]
  (identical? (.-target ev) (.-currentTarget ev)))

(defn handleBackgroundClick [menu send]
  (fn [ev]
    (when (isCurrentTarget ev)
      (send model/setMenu {:position [(.-clientX ev) (.-clientY ev)]}))))

(defnc Something [{:keys [send id title position]}]
  ($ ManagedWindow
    {:title (or title "Something")
     :onClose #(send model/removeCard id)
     :position position}
    "not yet implemented"))

(defnc MenuItem [{:keys [title icon cmp position send]}]
  (d/li
    {:onClick
     (fn [ev]
       (send model/setMenu nil)
       (send model/addCard {:type cmp :position position}))}
    (d/div
      {:class "icon"}
      ($ Icon {:icon icon}))
    title))

(defnc TestMenu [props]
  ($ MenuList
    ($ MenuItem {:title "Namespace Browser"
                 :icon "list"
                 :cmp #'NamespaceBrowserCard
                 & props})
    ($ MenuItem {:title "Workspace"
                 :icon "window-maximize"
                 :cmp #'Something
                 & props})))

;; pinned: moveBy position [200 0]
;; not pinned: current position + close menu

(defnc App []
  (let [[state send] (hook/use-state model/init)
        menu (:menu state)
        cards (:cards state)]
    (d/div
      {:style {:height "100%"}
       :onClick (handleBackgroundClick menu send)}
      #_(d/pre {:style {:position "absolute"}}
          (pp-str state))
      (when-let [{:keys [drag position]} menu]
        ($ WindowView
          {:title "Menu"
           :onClose #(send model/setMenu nil)
           :position position :onDragMove #(send model/moveMenu %)
           :drag drag :setDrag #(send model/setMenuDrag %)}
          ($ TestMenu {:send send :position position})))
      (for [[id card] cards]
        (let [cmp @(:type card)
              rest (dissoc card :type)]
          ($ cmp {:key (str id) :send send :id id & rest}))))))


(comment

  (for [[id card] cards
        type @(:type card)
        rest (dissoc card :type)]
    (d/pre (pp-str type) (pp-str {:key (str id) :id id :rest rest})))

  ($ ManagedWindow {:title "First"}
    (d/h2 "1"))
  ($ ManagedWindow {:title "Second" :position [500 150]}
    (d/h2 "2")))




(defn start
  []
  (js/console.log "Hello")
  (react-dom/render
      ($ App)
      (.getElementById js/document "app")))

(defn ^:after-load after-load
  []
  (start))

(defonce start-once (do (start) true))
