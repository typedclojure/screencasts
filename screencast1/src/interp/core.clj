(ns interp.core
  (:require [clojure.core.typed :as t]))

(t/defalias AST
  (t/Rec [AST]
    (t/U '{:op ':lambda
           :name t/Sym
           :body AST}
         '{:op ':if
           :test AST
           :then AST
           :else AST}
         '{:op ':local
           :name t/Sym}
         '{:op ':let
           :name t/Sym
           :init AST
           :body AST}
         '{:op ':const
           :val t/Any}
         '{:op ':app
           :rator AST
           :rand AST})))

(t/defalias LCSyntax 
  "This is an expression in lambda calculus."
  t/Any)

(t/defalias CLJSyntax 
  "A Clojure expression"
  t/Any)

(defmacro check-input [syn tst]
  `(assert ~tst (str "Bad input: " ~syn)))

(defn bad-input [syn]
  (throw (Exception. (str "Bad input: " (pr-str syn)))))

(t/ann analyze [LCSyntax -> AST])
(defn analyze [syn]
  (cond
    (number? syn) {:op :const
                   :val syn}
    (keyword? syn) {:op :const
                    :val syn}
    (symbol? syn) {:op :local
                   :name syn}
    (and (list? syn)
         (sequential? syn))
    (case (first syn)
      let
      (let [_ (check-input syn (#{3} (count syn)))
            [_ b body] syn
            _ (check-input b (and (vector? b)
                                  (#{2} (count b))))
            [n rhs] b
            _ (check-input n (symbol? n))]
        {:op :let
         :name n
         :init (analyze rhs)
         :body (analyze body)})

      lambda
      (let [_ (check-input syn (#{3} (count syn)))
            [_ b body] syn
            _ (check-input b (and (vector? b)
                                  (#{1} (count b))))
            [n] b
            _ (check-input n (symbol? n))]
        {:op :lambda
         :name n
         :body (analyze body)})

      if
      (let [_ (check-input syn (#{4} (count syn)))
            [_ test then else] syn]
        {:op :if
         :test (analyze test)
         :then (analyze then)
         :else (analyze else)})

      (cond
        (#{2} (count syn))
        {:op :app
         :rator (analyze (first syn))
         :rand  (analyze (second syn))}

        :else (bad-input syn)))

    :else (bad-input syn)))

(analyze 1)
(analyze :a)
(analyze 'a)

(analyze '(if 1 2 3))
(analyze '(lambda [a] a))
(analyze '(let [a 1] a))
(analyze '(let [a (lambda [x] x)] (a 1)))

(t/ann emit [AST -> CLJSyntax])
(defmulti emit :op)

(defmethod emit :const
  [{:keys [val]}]
  val)

(defmethod emit :lambda
  [{:keys [name body]}]
  (list 'fn [name] (emit body)))

(defmethod emit :if
  [{:keys [test then else]}]
  (list 'if 
        (emit test)
        (emit then)
        (emit else)))

(defmethod emit :local
  [{:keys [name]}]
  name)

(defmethod emit :let
  [{:keys [name init body]}]
  (list 'let
        [name (emit init)]
        (emit body)))

(defmethod emit :app
  [{:keys [rator rand]}]
  (map emit [rator rand]))

(-> '(lambda [a] 1)
    analyze
    emit)

(-> '(if (lambda [a] 1) 2 3)
    analyze
    emit
    #_eval)

(-> '(lambda [x] x)
    analyze
    emit
    #_eval)

(-> '(let [a (lambda [x] x)]
       a)
    analyze
    emit
    #_eval)

(-> '(let [a (lambda [x] x)]
       (a 1))
    analyze
    emit
    eval)
