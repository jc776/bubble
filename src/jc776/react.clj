(ns jc776.react
  (:require
    [helix.core]
    [helix.impl.analyzer :as hana]))

;; A React component is replaced when the function reference changes
;; So, we can retain hooks & state by retaining the same function
;; - Always re-define [Type]Impl to be the latest code
;; - Then, [Type] is a wrapper pointing at that one
;; - Only change (re-define) wrapper when hooks change

(defmacro defnc [type params & body]
  (let [type-impl (symbol (str type "Impl"))
        fully-qualified-name (str *ns* "/" type)
        hooks (pr-str (hana/find-hooks body))]
   `(do
      (helix.core/defnc ~type-impl ~params ~@body)
      (when-not
        (and (cljs.core/exists? ~type) ;; as in defonce
             (jc776.react/signature-equal? ~fully-qualified-name ~hooks)
             (not ~(-> type meta :reset)))
        (swap! jc776.react/used-hooks assoc ~fully-qualified-name ~hooks)
        (def ~type (fn [& args#] (apply ~type-impl args#)))))))
