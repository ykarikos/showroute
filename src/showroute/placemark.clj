(ns showroute.placemark
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]))

(def category-mapping
  {nil "car"
   "Baari" "beer"
   "Bed & Breakfast" "hotel"
   "Driving" "car"
   "Grilli" "restaurant"
   "Hiihtokeskus" "mountain"
   "Hotelli" "hotel"
   "Huoltoasema" "store"
   "Italialainen" "restaurant"
   "Julkisen liikenteen pysäkki" "walk"
   "Järvi" "swim"
   "Katolinen kirkko" "church"
   "Kukkakauppa" "store"
   "Köysirata" "mountain"
   "Leipomo" "store"
   "Leirintäalue" "camping"
   "Leivonnaiset" "store"
   "Lentokenttä" "airplane"
   "Linja-autoasema" "walk"
   "Linna" "castle"
   "Luonnonsuojelualue" "walk"
   "Maatila" "hotel"
   "Majatalo" "hotel"
   "Meren antimet" "restaurant"
   "Moving" "walk"
   "Painotalo" "store"
   "Palatsi" "castle"
   "Pizza" "restaurant"
   "Puisto" "walk"
   "Pysäköintialue" "walk"
   "Rakennustarvikeliike" "store"
   "Ravintola" "restaurant"
   "Stabilimento balneare" "swim"
   "Supermarketti" "store"
   "Teemapuisto" "walk"
   "Tiedemuseo" "walk"
   "Täysihoitola" "hotel"
   "Uimahalli" "swim"
   "Vierasmaja" "hotel"
   "Viinibaari" "beer"
   "Villieläinpuisto" "walk"
   "Vuoristomaja" "hotel"
   "Walking" "walk"
   "Yleinen kylpylä" "swim"})

(defn- select-tags [tag-names tags]
  (->> tags
       (filter #(tag-names (:tag %)))
       first))

(defn- swap-coordinates [c]
  (let [coordinates (clojure.string/split c #",")]
    {:lat (-> coordinates second Float/parseFloat)
     :lng (-> coordinates first Float/parseFloat)}))

(defn- get-category [p]
 (->> p :content (select-tags #{:ExtendedData}) :content
      (filter #(and (= (:tag %) :Data)
                    (= "Category" (-> % :attrs :name))))
      first :content first :content first
      (get category-mapping)))


(defn- coordinates-in-placemark [p]
  (let [coordinates (->> p :content (select-tags #{:Point :LineString})
                         :content (select-tags #{:coordinates})
                         :content)
        coordinates-list (clojure.string/split (first coordinates) #" ")
        category (get-category p)
        timestamp (->> p :content (select-tags #{:TimeSpan})
                       :content (select-tags #{:end})
                       :content first)]
    (->> coordinates-list
      (map swap-coordinates)
      (map #(merge % {:category category
                      :timestamp timestamp})))))

(defn- average [x y]
  (/ (+ x y) 2.0))

(defn parse-kml-coordinates [file]
  (let [kml-parsed (xml/parse (io/file (io/resource file)))
        coordinates (->> kml-parsed :content first :content
                         (filter #(= (:tag %) :Placemark))
                         (map coordinates-in-placemark)
                         flatten)
        max-lat (->> (map :lat coordinates) (reduce max))
        min-lat (->> (map :lat coordinates) (reduce min))
        max-lng (->> (map :lng coordinates) (reduce max))
        min-lng (->> (map :lng coordinates) (reduce min))]
    {:coordinates coordinates
     :center {:lat (average max-lat min-lat)
              :lng (average max-lng min-lng)}}))
