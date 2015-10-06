(ns reagent-motion-demos.demo3
  (:require [reagent.core :as reagent]
            [goog.object :as gobj]
            [reagent-motion-demos.common :as hc]))

(def NUM 4)

(def spring-config #js [300 50])

(defn handle-mouse-down
  [state i press-y e]
  (swap! state assoc
         :delta (- (gobj/get e "pageY")
                   press-y)
         :mouse press-y
         :is-pressed? true
         :last-press i))

(defn handle-touch-start
  [state i press-y e]
  (handle-mouse-down state i press-y (aget (.-touches e) 0)))

(defn Child
  [d]
  (let [[x i state data] (:children d)]
    [:div
     {:style {:position "absolute"
              :width "320px"
              :height "90px"
              :overflow "visible"
              :pointer-events "auto"
              :transform-origin "50% 50% 0px"
              :border-radius "4px"
              :color "rgb(153, 153, 153)"
              :line-height "96px"
              :padding-left "32px"
              :font-size "24px"
              :font-weight "400"
              :background-color "rgb(255, 255, 255)"
              :box-sizing "border-box"
              :-webkit-box-sizing "border-box"
              :box-shadow (str "rgba(0,0,0,0.2) 0px "
                               (gobj/get x "shadow") "px "
                               (* 2 (gobj/get x "shadow")) "px "
                               "0px")
              :transform (str "translateY("
                              (gobj/get x "y") "px) "
                              "scale(" (gobj/get x "scale") ")")
              :z-index   (if (= i (:last-press data))
                           99
                           i)}
      :on-mouse-down (partial handle-mouse-down state i (gobj/get x "y"))
      :on-touch-start (partial handle-touch-start state i (gobj/get x "y"))}
     (inc i)]))

(def Child-comp (reagent/reactify-component Child))

(defn handle-mouse-move
  [state e]
  (swap! state
         (fn [data]
           (if (:is-pressed? data)
             (let [mouse   (- (gobj/get e "pageY") (:delta data))
                   row     (hc/clamp (js/Math.round (/ mouse 100))
                                     0
                                     (dec NUM))
                   o       (hc/re-insert (:order data) (hc/index-of (:order data) (:last-press data)) row)]
               (assoc data
                      :mouse mouse
                      :order o))
             data))))

(defn handle-mouse-up
  [state e]
  (swap! state assoc :is-pressed? false :delta 0))

(defn handle-touch-move
  [state e]
  (.preventDefault e)
  (handle-mouse-move state (aget (.-touches e) 0)))

(defn show-demo
  [state data]
  [:div.display-flex {:style {:cursor "url('img/cursor.png') 39 39, auto"
                              :user-select "none"
                              :background-color "#EEE"
                              :color "#FFF"
                              :position "absolute"
                              :width "100%"
                              :height "100%"
                              :font "28px/1em \"Helvetica\""
                              :align-items "center"
                              :justify-content "center"
                              :-webkit-align-items "center"
                              :-webkit-justify-content "center"}
                      :on-mouse-move (partial handle-mouse-move state)
                      :on-mouse-up (partial handle-mouse-up state)
                      :on-touch-move (partial handle-touch-move state)
                      :on-touch-end (partial handle-mouse-up state)}
   [:div {:style {:width "320px"
                  :height "400px"}}
    (for [i  (range NUM)
          :let  [j       (hc/index-of (:order data) i)
                 style   (if (and (= i (:last-press data))
                                  (:is-pressed? data))
                           {:scale (hc/spring 1.1 spring-config)
                            :shadow (hc/spring 16 spring-config)
                            :y      (:mouse data)}
                           {:scale (hc/spring 1 spring-config)
                            :shadow (hc/spring 1 spring-config)
                            :y      (hc/spring (* 100 (dec j)) spring-config)})]]
      ^{:key i} [hc/Motion
                 {:style style}
                 (fn [x]
                   (reagent/create-element Child-comp #js {} [x i state data]))])]])
