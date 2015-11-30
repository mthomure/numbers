(ns numbers.util)

(defn vec-shape [data]
  (->> data (into []) (iterate first) (take-while coll?) (mapv count)))
