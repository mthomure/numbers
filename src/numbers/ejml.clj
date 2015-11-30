(ns numbers.ejml
  (:import [org.ejml.data DenseMatrix64F])
  (:require [numbers.util :refer [vec-shape]]))

(defn array
  "Create array from 1D data and shape."
  [data shape]
  (assert (= 2 (count shape)))
  (let [[n m] shape]
    (DenseMatrix64F. n m true (double-array data))))

;; TODO: zeros ones rand

(defn vec-> [data]
  (assert (coll? data))
  (let [shape (vec-shape data)
        size (reduce * shape)
        data (flatten data)]
    (assert (= (count data) size) "ragged arrays are not supported")
    (array (map double data) shape)))

(defn shape [mat]
  [(.numRows mat) (.numCols mat)])

(defn size
  ([mat] (reduce * (shape mat)))
  ([mat dim] (nth (shape mat) dim)))

;; TODO: add sub

(defn sum
  ([mat])
  ([mat dim]))

(defn mean
  ([mat] (/ (sum mat) (size mat)))
  ([mat dim] (/ (sum mat dim) (size mat dim))))
