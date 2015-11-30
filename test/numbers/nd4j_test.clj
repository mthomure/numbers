(ns numbers.nd4j-test
  (:refer-clojure :exclude [rand min max])
  (:import [org.nd4j.linalg.factory Nd4j])
  (:require [midje.sweet :refer :all]
            [numbers.nd4j :refer :all]))

(def EMPTY-ARRAY (Nd4j/create (double-array 0)))

;; (facts
;;  (vec-> nil) => EMPTY-ARRAY
;;  (vec-> []) => EMPTY-ARRAY
;;  (vec-> [1]) => (array [1] [1])
;;  (vec-> [[[1 2 3] [4 5 6]]]) => (array [1 2 3 4 5 6] [1 2 3])
;;  (vec-> [[[1 2 3]] [[4 5 6]]]) => (array [1 2 3 4 5 6] [2 1 3]))

;; (facts
;;  "about scalars"
;;  (let [mat (array [9.0] [1 1])]
;;    (vec-> [9.0]) => mat
;;    (->vec mat) => [[9.0]]
;;    (ndim mat) => 2
;;    (shape mat) => [1 1]
;;    (size mat) => 1
;;    (atype mat) => :scalar))

;; (facts
;;  "about row vectors"
;;  (let [mat (array [0 1 2] [1 3])]
;;    (vec-> [[0 1 2]]) => mat
;;    (->vec mat) => [[0. 1. 2.]]
;;    (ndim mat) => 2
;;    (shape mat) => [1 3]
;;    (size mat) => 3
;;    (atype mat) => :row))

;; (facts
;;  "about column vectors"
;;  (let [mat (array [0 1 2] [3 1])]
;;    (vec-> [[0] [1] [2]]) => mat
;;    (->vec mat) => [[0.] [1.] [2.]]
;;    (ndim mat) => 2
;;    (shape mat) => [3 1]
;;    (size mat) => 3
;;    (atype mat) => :column))

;; (facts
;;  "about matrices"
;;  (let [mat (array [0 1 2 3 4 5] [2 3])]
;;    (vec-> [[0 1 2] [3 4 5]]) => mat
;;    (->vec mat) => [[0. 1. 2.] [3. 4. 5.]]
;;    (ndim mat) => 2
;;    (shape mat) => [2 3]
;;    (size mat) => 6
;;    (atype mat) => :matrix))

;; (facts
;;  "about tensors"
;;  (let [mat (array [0 1 2 3 4 5] [1 3 2])]
;;    (vec-> [[[0 1] [2 3] [4 5]]]) => mat
;;    (->vec mat) => [[[0. 1.] [2. 3.] [4. 5.]]]
;;    (ndim mat) => 3
;;    (shape mat) => [1 3 2]
;;    (size mat) => 6
;;    (atype mat) => :tensor))

;; (facts
;;  "about equal arrays"
;;  (fact
;;   "1d arrays are equal"
;;   (= (array [0 1 2] [3]) (array [0 1 2] [3])) => true)
;;  (fact
;;   "2d arrays are equal"
;;   (= (array [0 1 2 3] [2 2]) (array [0 1 2 3] [2 2])) => true)
;;  (fact
;;   "symmetric matrix and its transpose"
;;   (let [a (array [0 1 1 3] [2 2])] (= a (transpose a)) => true))
;;  (fact
;;   "3d arrays are equal"
;;   (= (array [0 1 2 3 4 5 6 7] [2 2 2])
;;      (array [0 1 2 3 4 5 6 7] [2 2 2])) => true))

;; (facts
;;  "about unequal arrays"
;;  (fact
;;   "different arrays of same shape"
;;   (= (array [0 1 2] [3]) (array [4 5 6] [3])) => false)
;;  (future-fact
;;   "row vector and its transpose"
;;   (let [a (array [0 1 2] [3 1])] (= a (transpose a)) => false))
;;  (fact
;;   "non-symmetric matrix and its transpose"
;;   (let [a (array [0 1 2 3] [2 2])] (= a (transpose a))) => false)
;;  (fact
;;   "same arrays of different shape"
;;   (= (array [0 1 2 3] [1 4]) (array [0 1 2 3] [2 2])) => false)
;;  (fact
;;   "different arrays of different shape"
;;   (= (array [0 1 2 3] [1 4]) (array [4 5 6 7] [2 2])) => false)
;;  (fact
;;   "different arrays of different size and shape"
;;   (= (array [0 1 2 3] [2 2]) (array [0 1 2 3 4 5 6 7] [2 2 2])) => false)
;;  (future-fact
;;   "1d array its sub-array"
;;   (= (array [0] [1]) (array [0 1 2] [3])) => false))
