(ns dnd.domain.campaign
  (:require [clojure.string :as str]
            [dnd.domain.common :refer [UUID]]
            [dnd.domain.user :refer [UserId]]
            [schema.core :as s]))

(defn valid-campaign-id?
  [id]
  (let [[type uuid] (str/split id #":")]
    (and (= "campaign" type)
         (nil? (s/check UUID uuid)))))

(s/defschema CampaignId
  (s/constrained s/Str valid-campaign-id? ::valid-campaign-id))

(s/defschema Campaign
  {:id      CampaignId
   :title   s/Str
   :dm      UserId
   :players [UserId]})
