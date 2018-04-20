(defproject dnd "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [com.andrewmcveigh/cljs-time "0.5.0"]
                 [org.clojure/core.async "0.2.391"]
                 [re-com "2.1.0"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [secretary "1.2.3"]
                 [metosin/compojure-api "2.0.0-alpha19"]
                 [yogthos/config "0.8"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring "1.6.3"]
                 [ring-cors "0.1.12"]
                 [org.apache.commons/commons-math3 "3.6"]
                 [venantius/accountant "0.2.4"]
                 [codax "1.2.0"]
                 [mount "0.1.12"]]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-less "1.7.5"]
            [lein-cljfmt "0.5.7"]]

  :cljfmt {:file-pattern #"\.clj[sxc]?$"}

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljc"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler dnd.handler/dev-handler}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]
                   [day8.re-frame/re-frame-10x "0.3.0"]
                   [day8.re-frame/tracing "0.5.0"]]

    :plugins      [[lein-figwheel "0.5.13"]
                   [lein-doo "0.1.8"]]}
   :prod {:dependencies [[day8.re-frame/tracing-stubs "0.5.0"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "src/cljc"]
     :figwheel     {:on-jsload "dnd.core/mount-root"}
     :compiler     {:main                 dnd.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload
                                           day8.re-frame-10x.preload]
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true
                                           "day8.re_frame.tracing.trace_enabled_QMARK_" true}
                    :external-config      {:devtools/config {:features-to-install :all}}}}

    {:id           "min"
     :source-paths ["src/cljs" "src/cljc"]
     :jar true
     :compiler     {:main            dnd.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    {:id           "test"
     :source-paths ["src/cljs" "src/cljc" "test/cljs" "test/cljc"]
     :compiler     {:main          dnd.runner
                    :output-to     "resources/public/js/compiled/test.js"
                    :output-dir    "resources/public/js/compiled/test/out"
                    :optimizations :none}}]}

  :main dnd.server

  :aot [dnd.server]

  :uberjar-name "dnd.jar"

  :prep-tasks [["cljsbuild" "once" "min"] ["less" "once"] "compile"])
