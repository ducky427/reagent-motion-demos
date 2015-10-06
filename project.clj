(defproject reagent-motion-demos "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljsjs/react "0.13.3-1"]
                 [cljsjs/react-motion "0.3.0-0"]
                 [reagent "0.5.1"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.4.0"]
            [lein-ancient "0.6.7"]
            [lein-cljfmt "0.3.0"]]

  :source-paths ["src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]

              :figwheel { :on-jsload "reagent-motion-demos.core/mount-root" }

              :compiler {:main reagent-motion-demos.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/motion_demo.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/motion_demo.js"
                         :main reagent-motion-demos.core
                         :optimizations :advanced
                         :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
