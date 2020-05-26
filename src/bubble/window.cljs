(ns bubble.window
  (:require
    [bubble.style :refer [Icon css-translate]]
    [bubble.model :refer [moveBy positionDelta]]
    [jc776.react :refer [defnc]]
    [jc776.pp :refer [pp-str]]
    [jc776.errorboundary :refer [resettable-boundary]]
    [helix.core :refer [$]]
    [helix.hooks :as hook]
    [helix.dom :as d]))

;; Window - a draggable window
;; - Avoid "focused", do not want modals
;; - Avoid "maximize/minimize" until needed
;; - Not sure how to have "resizable" + "top-level state knows about size"

;; would prefer (onDragMove [(.-movementX ev) (.-movementY ev)])
;; it is is always [0 0] on iOS

(defn screenPos [ev]
  [(.-screenX ev) (.-screenY ev)])

(defn handleDragStart [setDrag prevPos]
  (fn [ev]
    (-> ev .-target (.setPointerCapture (.-pointerId ev)))
    (set! (.-current prevPos) (screenPos ev))
    (setDrag (.-pointerId ev))))

(defn handleDragMove [drag onDragMove prevPos]
  (fn [ev]
    (when (identical? (.-pointerId ev) drag)
      (let [nextPos (screenPos ev)]
        (when (exists? (.-current prevPos))
          (onDragMove (positionDelta (.-current prevPos) nextPos)))
        (set! (.-current prevPos) nextPos)))))

(defn handleDragEnd [drag setDrag prevPos]
  (fn [ev]
    (when (identical? (.-pointerId ev) drag)
      (-> ev .-target (.releasePointerCapture (.-pointerId ev)))
      (set! (.-current prevPos) nil)
      (setDrag false))))

(defnc WindowView [{:keys [send position drag setDrag onDragMove title children onClose]}]
  (let [prevPos (hook/use-ref nil)]
    (d/div
      {:class ["window focused" (when drag "dragging")]
       :style {:transform (css-translate position)}}
      ;; ... resizer blobs
      (d/div {:class "window-border"}
        (d/div {:class "window-titlebar"}
          (d/div
            {:class "window-title"
             :onPointerDown (handleDragStart setDrag prevPos)
             :onPointerMove (when drag (handleDragMove drag onDragMove prevPos))
             :onPointerUp (handleDragEnd drag setDrag prevPos)}
            (d/span title))
          (d/div {:class "window-controls"}
            (when onClose
              (d/span
                {:class "window-button window-close" :title "close"
                 :onClick #(onClose)}
                ($ Icon {:icon "window-close"})))))
        (d/div {:class "window-content"}
          ($ resettable-boundary
            children))))))

(defnc ManagedWindow [props]
  (let [[position setPosition] (hook/use-state (or (:position props [100 100])))
        [drag setDrag] (hook/use-state false)]
    ($ WindowView
      {:position position
       :drag drag :setDrag setDrag
       :onDragMove #(setPosition moveBy %)
       & (dissoc props :position)}
      (:children props))))

(comment
  (d/span {:class "window-button window-menu" :title "menu"}
    ($ Icon {:icon "bars"}))
  (d/span {:class "window-button window-unmin" :title "expand"}
    ($ Icon {:icon "window-maximize"}))
  (d/span {:class "window-button window-min" :title "collapse"}
    ($ Icon {:icon "window-minimize"}))
  (d/span {:class "window-button window-max" :title "maximize"}
    ($ Icon {:icon "window-maximize"})))
