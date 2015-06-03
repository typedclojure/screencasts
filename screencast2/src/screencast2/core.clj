(ns ^:core.typed screencast2.core
  (:require [clojure.core.typed :as t]))

(t/tc-ignore
  (alter-meta! *ns* assoc :core.typed true))

(t/ann foo [t/Num -> t/Num])
(defn foo
  "I'm ill-typed"
  [x]
  (inc x))
