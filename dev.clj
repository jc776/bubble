(require 'rebel-readline.main)
(require '[figwheel.main.api :as fw])
(require '[clojure.tools.deps.alpha.repl :refer [add-lib]])

(figwheel.main.api/start {:mode :serve} "dev")
(rebel-readline.main/-main)

(println "Bye")
(System/exit 0)
