(ns reagent-motion-demos.demo2
  (:require [reagent.core :as reagent]
            [goog.object :as gobj]
            [reagent-motion-demos.common :as hc]))

(def NUM 11)
(def WIDTH 70)
(def HEIGHT 90)

(def layout (into {} (map (fn [x]
                            [x
                             [(* WIDTH (mod x 3))
                              (* HEIGHT (Math/floor (/ x 3)))]])
                          (range NUM))))

(def colors ["#EF767A" "#456990" "#49BEAA" "#49DCB1" "#EEB868"
             "#EF767A" "#456990" "#49BEAA" "#49DCB1" "#EEB868"
             "#EF767A"])

(defn handle-mouse-down
  [state i press-x press-y e]
  (swap! state assoc
         :last-press i
         :is-pressed? true
         :delta       [(- (.-pageX e)
                          press-x)
                       (- (.-pageY e)
                          press-y)]
         :mouse       [press-x press-y]))

(defn handle-touch-start
  [state i press-x press-y e]
  (handle-mouse-down state i press-x press-y (aget (.-touches e) 0)))

(defn Child
  [d]
  (let [[i b state data x y visual-pos] (:children d)]
    [:div
     {:on-mouse-down (partial handle-mouse-down state i x y)
      :on-touch-start (partial handle-touch-start state i x y)
      :style {:position           "absolute"
              :border             "1px solid black"
              :border-radius      "99px"
              :width              "50px"
              :height             "50px"
              :background-color   (nth colors i)
              :-webkit-transform   (str "translate3d("
                                        (gobj/get b "translateX") "px,"
                                        (gobj/get b "translateY") "px,0) "
                                        "scale(" (gobj/get b "scale") ")")
              :transform          (str "translate3d("
                                       (gobj/get b "translateX") "px,"
                                       (gobj/get b "translateY") "px,0) "
                                       "scale(" (gobj/get b "scale") ")")
              :z-index            (if (= i (:last-press data))
                                    99
                                    visual-pos)
              :box-shadow         (str (gobj/get b "boxShadow") "px 5px 5px rgba(0,0,0,0.5)")}}]))

(def Child-comp (reagent/reactify-component Child))

(defn parent
  [i state data]
  (let [[x y]          (:mouse data)
        visual-pos     (hc/index-of (:order data) i)
        [[x y] style]  (if (and (= i (:last-press data))
                                (:is-pressed? data))
                         [[x y] {:translateX  x
                                 :translateY  y
                                 :scale       (hc/spring 1.2 #js [180 10])}]
                         (let [[x y]  (layout visual-pos)]
                           [[x y] {:translateX  (hc/spring x #js [120 17])
                                   :translateY  (hc/spring y #js [120 17])
                                   :scale       (hc/spring 1 #js [180 10])}]))
        style            (assoc style :boxShadow (hc/spring (/ (/ (- x
                                                                     (- (* 3 WIDTH)
                                                                        50))
                                                                  2)
                                                               15)
                                                            #js [180 10]))]
    [hc/Motion
     {"style" style}
     (fn [b]
       (reagent/create-element Child-comp #js {} [i b state data x y visual-pos]))]))

(defn handle-mouse-move
  [state e]
  (swap! state
         (fn [data]
           (if (:is-pressed? data)
             (let [[dx dy]   (:delta data)
                   mouse     [(- (.-pageX e)
                                 dx)
                              (- (.-pageY e)
                                 dy)]
                   col       (hc/clamp (Math/floor (/ (nth mouse 0)
                                                      WIDTH))
                                       0
                                       2)
                   row       (hc/clamp (Math/floor (/ (nth mouse 1)
                                                      HEIGHT))
                                       0
                                       (Math/floor (/ NUM 3)))
                   i         (+ (* row 3)
                                col)]
               (assoc data
                      :mouse mouse
                      :order (hc/re-insert (:order data) (hc/index-of (:order data) (:last-press data)) i)))
             data))))

(defn handle-mouse-up
  [state e]
  (swap! state assoc
         :is-pressed? false
         :delta [0 0]))

(defn handle-touch-move
  [state e]
  (.preventDefault e)
  (handle-mouse-move state (aget (.-touches e) 0)))

(defn show-demo
  [state data]
  [:div.display-flex
   {:style {:height "80%"
            :width "80%"
            :position "absolute"
            :align-items "center"
            :justify-content "center"
            :-webkit-align-items "center"
            :-webkit-justify-content "center"}
    :on-mouse-move (partial handle-mouse-move state)
    :on-mouse-up (partial handle-mouse-up state)
    :on-touch-move (partial handle-touch-move state)
    :on-touch-end (partial handle-mouse-up state)}
   [:div
    {:style {:width "190px"
             :height "320px"}}
    (for [k  (:order data)]
      ^{:key k} [parent k state data])]])
