(ns reagent-motion-demos.demo1
  (:require [reagent.core :as reagent]
            [goog.object :as gobj]
            [reagent-motion-demos.common :as hc]))

(defn get-styles
  [state-data]
  (fn [xs]
    (.map xs (fn [_ i]
               (if (zero? i)
                 (clj->js state-data)
                 #js {"x" (hc/spring (-> xs
                                         (aget (dec i))
                                         (gobj/get "x"))
                                     (.-gentle hc/presets))
                      "y" (hc/spring (-> xs
                                         (aget (dec i))
                                         (gobj/get "y"))
                                     (.-gently hc/presets))})))))

(defn Child
  [i x]
  [:div
   {:style {:-webkit-transform (str "translate3d("
                                   (- (gobj/get x "x") 25) "px, "
                                   (- (gobj/get x "y") 25) "px, 0)")
            :transform (str "translate3d("
                            (- (gobj/get x "x") 25) "px, "
                            (- (gobj/get x "y") 25) "px, 0)")
            :z-index (- 5 i)
            :background-image (str "url(img/" i ".jpg)")
            :border-radius "99px"
            :background-color "white"
            :width "50px"
            :height "50px"
            :border "3px solid white"
            :position "absolute"
            :background-size "50px"}}])

(defn Parent
  [d]
  (let [[balls state]  (:children d)]
    [:div
     {:style {:width "80%"
              :height "80%"
              :position "absolute"
              :background-color "#EEE"}
      :on-mouse-move #(swap! state
                             assoc
                             :x (.-pageX %)
                             :y (.-pageY %))
      :on-touch-move (fn [xs]
                       (let [e  (aget (.-touches xs) 0)]
                         (swap! state
                                assoc
                                :x (.-pageX e)
                                :y (.-pageY e))))}
     (for [[i x] (map-indexed vector balls)]
       ^{:key i} [Child i x])]))

(def Parent-comp (reagent/reactify-component Parent))

(defn Demo
  [state]
  [hc/StaggeredMotion {"defaultStyles" (repeat 6 {:x 0 :y 0})
                       "styles" (get-styles @state)}
   (fn [balls]
     (reagent/create-element Parent-comp #js {} [balls state]))])

(defn show-demo
  [state data]
  [Demo state])
