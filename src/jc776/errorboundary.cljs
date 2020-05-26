(ns jc776.errorboundary
  (:require
    [jc776.react :refer [defnc]]
    [helix.core :refer [defcomponent]]
    [helix.core :refer [$]]
    [helix.hooks :as hook]
    [helix.dom :as d]))

(defcomponent error-boundary
  (constructor [this]
    (set! (.-state this) #js {:error nil}))

  ^:static
  (getDerivedStateFromError [this error]
    #js {:error error})

  (render [this]
    (if-not (.. this -state -error)
      (.. this -props -children)
      (d/div
        (d/button {:onClick #((.. this -props -setKey) inc)} "OK")
        (d/pre (d/code (pr-str (.. this -state -error))))))))

(defnc resettable-boundary [{:keys [children]}]
  (let [[key setKey] (hook/use-state 0)]
    ($ error-boundary
      {:key key :setKey setKey}
      children)))
