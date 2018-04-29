(ns ^{:clojure.tools.namespace.repl/load false} revolt.plugins.rebel
  (:require [io.aviso.ansi]
            [clojure.tools.logging :as log]
            [rebel-readline.clojure.main]
            [rebel-readline.core]
            [revolt.plugin :refer [Plugin create-plugin]]))

(defn init-plugin
  "Initializes a rebel REPL."

  [config]
  (reify Plugin
    (activate [this ctx]
      (rebel-readline.core/ensure-terminal
       (rebel-readline.clojure.main/repl
        :init (fn []
                (when-let [ns-str (:init-ns config)]
                  (let [ns-sym (symbol ns-str)]
                    (try
                      (log/info "Loading Clojure code, please wait...")
                      (require ns-sym)
                      (in-ns ns-sym)

                      (catch Exception e
                        (log/error "Failed to require dev, this usually means there was a syntax error." (.getMessage e))
                        (log/error "Please correct it, and enter (fixed!) to resume development."))))))))

      ;; enforce global annihilation
      ((.terminate ctx)))

    (deactivate [this ret]
      (log/debug "closing rebel"))))
