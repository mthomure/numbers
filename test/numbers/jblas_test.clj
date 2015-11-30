(ns numbers.jblas-test
  (:refer-clojure :exclude [rand min max])
  (:import [org.jblas DoubleMatrix])
  (:require [midje.sweet :refer :all]
            [numbers.jblas :refer :all]))

(def EMPTY-ARRAY (DoubleMatrix. 0 0 (double-array 0)))

(facts
 (array [0 1 2 3] [2 2]) => (DoubleMatrix. 2 2 (double-array [0 2 1 3])))

(facts
 "about matrices"
 (let [mat (array [0 4 5 5 3 4] [2 3])
       data [[0. 4. 5.] [5. 3. 4.]]]
   (clj-> data) => mat
   (->clj mat) => data
   (shape mat) => [2 3]
   (size mat) => 6
   (sum mat) => 21.
   (sum mat 0) => (array [9 12] [2 1])
   (sum mat 1) => (array [5 7 9] [1 3])
   (mean mat) => 3.5
   (mean mat 0) => (array [3 4] [2 1])
   (mean mat 1) => (array [5/2 7/2 9/2] [1 3])
   ))

(facts
 (mtype 2.) => :scalar
 (mtype (array [0 1 2 3] [2 2])) => :matrix
 (mtype (array [0 1 2 3] [1 4])) => :row
 (mtype (array [0 1 2 3] [4 1])) => :column)

(facts
 "about argmin/argmax"
 (let [mat (array [0 4 5 5 3 4] [2 3])
       data [[0. 4. 5.] [5. 3. 4.]]]
   (argmax mat) => [1 0]
   (argmax mat 0) => [2 0]
   (argmax mat 1) => [1 0 0]
   (argmin mat) => [0 0]
   (argmin mat 0) => [0 1]
   (argmin mat 1) => [0 1 1]))

(facts
 "about equal arrays"
 (fact
  "matrices are equal"
  (= (array [0 1 2 3 4 5] [2 3]) (array [0 1 2 3 4 5] [2 3])) => true)
 (fact
  "symmetric matrix and its transpose"
  (let [a (array [0 1 1 3] [2 2])] (= a (transpose a)) => true)))

(facts
 "about unequal arrays"
 (fact
  "different arrays of same shape"
  (= (array [0 1 2] [1 3]) (array [4 5 6] [1 3])) => false)
 (fact
  "row vector and its transpose"
  (let [a (array [0 1 2] [3 1])] (= a (transpose a)) => false))
 (fact
  "non-symmetric matrix and its transpose"
  (let [a (array [0 1 2 3] [2 2])] (= a (transpose a))) => false)
 (fact
  "same arrays of different shape"
  (= (array [0 1 2 3] [1 4]) (array [0 1 2 3] [2 2])) => false)
 (fact
  "different arrays of different shape"
  (= (array [0 1 2 3] [1 4]) (array [4 5 6 7] [2 2])) => false)
 (fact
  "different arrays of different size and shape"
  (= (array [0 1 2 3] [2 2]) (array [0 1 2 3 4 5 6 7] [2 4])) => false))
