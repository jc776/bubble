(ns bubble.model)

;; positions

(defn containPosition [x y]
  [(max 0 x) (max 0 y)])

(defn moveBy [[x y] [dx dy]]
  (containPosition (+ x dx) (+ y dy)))

(defn positionDelta [[x1 y1] [x2 y2]]
  [(- x2 x1) (- y2 y1)])

;; top-level model

(def init
  {:menu nil
   :cards {}})

(defn addCard [model {:keys [id] :as card}]
  (assoc-in model [:cards (or id (random-uuid))] card))

(defn removeCard [model id]
  (update model :cards dissoc id))

(defn moveCard [model id amount]
  (update-in model [:cards id :position] moveBy amount))

(defn setCardDrag [model id data]
  (assoc-in model [:cards id :drag] data))

(defn setMenu [model data]
  (assoc model :menu data))

(defn moveMenu [model amount]
  (update-in model [:menu :position] moveBy amount))

(defn setMenuDrag [model data]
  (assoc-in model [:menu :drag] data))
