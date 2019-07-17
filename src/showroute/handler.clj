(ns showroute.handler
  (:require [compojure.core :refer :all]
            [hiccup.page :refer [include-js include-css html5]]
            [showroute.placemark :refer [parse-kml-coordinates]]
            [compojure.route :as route]
            [config.core :refer [env]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

(def kml-file "route.kml")

(def title "Route on map")
(def google-maps
  (str "https://maps.googleapis.com/maps/api/js?key="
       (env :google-maps-api-key)
       "&callback=initMap"))

(defn- head []
  [:head
   [:meta {:charset "utf-8"}]
   [:title title]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-js "/main.js")
   (include-css "/style.css")])

(defn- index-page []
  (html5
    (head)
    [:body
     [:h1 title]
     [:div#buttons
      [:button#animButton {:title "start/stop"
                           :onclick "startStopAnimate()"} "▶︎"]
      [:button {:title "restart"
                :onclick "restartAnimation()"} "↻"]
      [:span#timestamp]]
     [:div#map]
     [:script {:async nil
               :defer nil
               :src google-maps}]]))

(defroutes app-routes
  (GET "/" [] (index-page))
  (GET "/coordinates" [] (response (parse-kml-coordinates kml-file)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-json-response
      (wrap-resource "public")
      wrap-content-type
      wrap-not-modified))
