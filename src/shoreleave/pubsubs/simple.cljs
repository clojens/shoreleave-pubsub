(ns shoreleave.pubsubs.simple
  "An extended pub/sub implementation built on Google's PubSub Object"
  (:require [goog.pubsub.PubSub :as pubsub]
            [shoreleave.pubsubs.protocols :as ps-protocols]))

;; Below is an implementation of IMessageBrokerBus built upon
;; Google Closure's [PubSub object](http://closure-library.googlecode.com/svn/docs/class_goog_pubsub_PubSub.html)
;; This will have all the properties of PubSub (ie: synchronous)

(extend-type goog.pubsub.PubSub
  ps-protocols/IMessageBrokerBus
  (subscribe [bus topic handler-fn]
    (.subscribe bus (ps-protocols/topicify topic) handler-fn))

  (subscribe-once [bus topic handler-fn]
    (.subscribeOnce bus (ps-protocols/topicify topic) handler-fn))

  #_(subscribe-> [bus & chain-handler-fns]
    (let [subscripts (partition 2 1 chain-handler-fns)]
      (when-not (empty? subscripts)
        (doseq [[t h] subscripts]
          (ps-protocols/subscribe bus t h)))))

  (unsubscribe [bus topic handler-fn]
    (.unsubscribe bus (ps-protocols/topicify topic) handler-fn))

  (publish
    ([bus topic data]
     (.publish bus (ps-protocols/topicify topic) data))
    ([bus topic data & more-data]
     (.publish bus (ps-protocols/topicify topic) (into [data] more-data))))

  IHash
  (-hash [bus] (goog.getUid bus)))

(defn subscribers-count
  "Given a bus and a topic, return the number of subscribers
  (registered handler functions)"
  [bus topic]
  (.getCount bus (ps-protocols/topicify topic)))

(defn bus
  "Get a simple bus"
  []
  (goog.pubsub.PubSub.))

