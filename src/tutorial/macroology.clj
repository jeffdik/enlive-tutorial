(ns tutorial.macroology
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.duck-streams :only [slurp*]])
  (:use [clojure.contrib.seq-utils :only [flatten]])
  (:use [clojure.contrib.java-utils :only [file]])
  (:import [java.io ByteArrayInputStream]))

;; change this line to match your environment
(def *webdir* "/Users/davidnolen/development/clojure/enlive-tutorial/src/tutorial/")

(defn to-in-s [str] (ByteArrayInputStream. (.getBytes str "UTF-8")))

(def *nodes-with-id*
     (html/selector #{[[:* (html/attr? :id)]]}))

(defn sel-for-node [{tag :tag, attrs :attrs}]
  (let [css-id (:id attrs)]
    `([~(keyword (str (name tag) "#" css-id))] (html/content (~(keyword css-id) ~'ctxt)))))

(defmacro templ [name rsrc]
  (let [nodes (html/select (html/html-resource (eval rsrc)) *nodes-with-id*)]
    `(html/deftemplate ~name ~rsrc
       [~'ctxt]
       ~@(reduce concat (map sel-for-node nodes)))))

(defmacro templ-str [name rsrc-str]
  (let [nodes (html/select (html/html-resource (to-in-s (eval rsrc-str))) *nodes-with-id*)]
    `(html/deftemplate ~name (to-in-s ~rsrc-str)
       [~'ctxt]
       ~@(reduce concat (map sel-for-node nodes)))))

(comment
  (def *markup* "<span id='foo'></span><div class='bar'><p id='bar'></p></div>")
  (templ-str foo *markup*)
  (apply str (foo {:foo "cool" :bar "awesome"}))

  (templ bar (file *webdir* "introspect.html"))
  (apply str (bar {:foo "cool" :bar "awesome"}))
  )