(defproject screencast2 "0.1.0-SNAPSHOT"
  :description "Introducing the core.typed REPL and integrated type checking"
  :url "http://typedclojure.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/core.typed "0.3.0-alpha5"]]
  :repl-options {:nrepl-middleware [clojure.core.typed.repl/wrap-clj-repl]})
