(ns reagent-motion-demos.common
  (:require [reagent.core :as reagent]
            [cljsjs.react-motion]))

(def Motion (reagent/adapt-react-class js/ReactMotion.Motion))
(def StaggeredMotion (reagent/adapt-react-class js/ReactMotion.StaggeredMotion))
(def TransitionMotion (reagent/adapt-react-class js/ReactMotion.TransitionMotion))


(def spring js/ReactMotion.spring)
(def presets js/ReactMotion.presets)

(defn index-of [coll value]
  (some (fn [[idx item]]
          (when (= value item)
            idx))
        (map-indexed vector coll)))

(defn clamp
  [n a b]
  (max (min n b) a))

(defn re-insert
  [xs from to]
  (let [[from to]   [(Math/min from to) (Math/max from to)]
        x           (nth xs from)]
    (vec (concat (subvec xs 0 from)
                 (subvec xs (inc from) (inc to))
                 [x]
                 (subvec xs (inc to))))))
