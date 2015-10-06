(ns reagent-motion-demos.core
  (:require [cljs.core :as core]))

(defmacro es6-array-map
  [ty]
  (let [f  (gensym)]
    `(aset (.-prototype ~ty) "map"
           (fn [~f]
             (core/this-as this#
                           (mapv ~f this#))))))
