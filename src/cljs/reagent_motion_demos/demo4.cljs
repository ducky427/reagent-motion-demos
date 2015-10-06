(ns reagent-motion-demos.demo4
  (:require [reagent.core :as reagent]
            [goog.object :as gobj]
            [reagent-motion-demos.common :as hc]))

(defn make-config
  [config offset photos height widths]
  (let [n   (count photos)]
    (loop [config     config
           i          0
           prev-left  offset]
      (if (= i n)
        config
        (let [[w h]  (nth photos i)]
          (recur
           (assoc config
                  i
                  {:left (hc/spring prev-left #js [170 26])
                   :height (hc/spring height #js [170 26])
                   :width (hc/spring (* (/ height h) w)  #js [170 26])})
           (inc i)
           (+ prev-left (nth widths i))))))))

(defn get-styles
  [state data]
  (let [photos              (:photos data)
        current             (:current data)
        [width height]      (nth photos current)
        widths              (mapv (fn [[w h]]
                                    (* (/ height h)
                                       w))
                                  photos)
        offset              (reduce - 0 (subvec widths 0 current))
        config              {:container {:height (hc/spring height)
                                         :width  (hc/spring width)}}]
    (make-config config offset photos height widths)))

(defn parse-int
  [x]
  (js/parseInt x 10))

(defn on-change
  [state e]
  (swap! state assoc :current (parse-int (-> e .-target .-value))))

(defn Child
  [d]
  (let [[styles state data]  (:children d)
        container            (gobj/get styles "container")
        photos               (:photos data)]
    [:div.display-flex {:style {:align-items "center"
                                :height "700px"
                                :position "relative"}}
     [:div {:style {:overflow "hidden"
                    :position "relative"
                    :margin   "auto"
                    :height   (gobj/get container "height")
                    :width    (gobj/get container"width")}}
      (for [i  (range (count photos))
            :let [s  (gobj/get styles (str i))]]
        [:img {:style {:position "absolute"
                       :background-color "lightgray"
                       :left (gobj/get s "left")
                       :height (gobj/get s "height")
                       :width (gobj/get s "width")}
               :key i
               :src (str "img/slider/" i ".jpg")}])]]))

(def Child-comp (reagent/reactify-component Child))

(defn show-demo
  [state data]
  [:div
   [:input {:type "range"
            :min 0
            :max (dec (count (:photos data)))
            :value (:current data)
            :on-change (partial on-change state)
            :style {:width "200px"}}]
   (:current data)
   [hc/TransitionMotion {:styles (get-styles state data)}
    (fn [x]
      (reagent/create-element Child-comp #js {} [x state data]))]])
