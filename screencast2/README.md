# Introducing the core.typed REPL and integrated type checking

So far core.typed has offered offline checking. The `check-ns`
function is exposed so programmers can type check namespaces when
they like.

Using nREPL middleware and a little monkey-patching, we can integrate
core.typed into the compilation pipeline.
This way you can use typed namespaces as you would a normal namespace,
except you automatically get type checking from core.typed.

## Setup

We assume you are using leiningen.

- Step 1:

Depend on core.typed 0.3.0-x and Clojure 1.7.0-x.

```clojure
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/core.typed "0.3.0-alpha5"]])
```

- Step 2:

Add the following middleware 
to your `project.clj`, or equivalent middleware 

```clojure
  :repl-options {:nrepl-middleware [clojure.core.typed.repl/wrap-clj-repl]}
```

- Step 3:

Add `^:core.typed` metadata to the `ns` form you want type checked.

## Usage

Use your favourite Clojure editor/IDE and use the standard REPL and load features.
