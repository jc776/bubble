(ns bootstrap.table
  (:require
    [jc776.react :refer [defnc]]
    [jc776.pp :refer [pp-str]]
    [helix.core :refer [$]]
    [helix.hooks :as hook]
    [helix.dom :as d]))

;; consider:
;; https://package.elm-lang.org/packages/billstclair/elm-sortable-table/1.2.0/

(defnc TableView [{:keys [cols key-fn cell-fn data]}]
  (d/table {:class "table"}
    (d/thead
      (d/tr
        (for [[name _] cols]
          (d/th {:key name} name))))
    (d/tbody
      (for [row data]
        (d/tr {:key (key-fn row)}
          (for [[name val-fn] cols]
            (d/td {:key name}
              (if cell-fn
                (cell-fn (val-fn row))
                (val-fn row)))))))))
