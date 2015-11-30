(ns numbers.jblas
  "Double-precision dense matrix operations."
  (:refer-clojure :exclude [rand])
  (:import [org.jblas DoubleMatrix])
  (:require [numbers.util :refer [vec-shape]]
            [clojure.string :as s]))

(defn- check [shape]
  (assert (= 2 (count shape)))
  shape)

(defn array [data shape]
  ;; jblas expects elements in column-major order. here, we transpose the
  ;; collection, and then re-transpose the matrix.
  (let [[m n] (check shape)
        data (flatten (apply map vector (partition n data)))]
    (DoubleMatrix. m n (double-array data))))

(defn ones [shape]
  (let [[m n] (check shape)]
    (DoubleMatrix/ones m n)))

(defn rand [shape]
  (let [[m n] (check shape)]
    (DoubleMatrix/rand m n)))

(defn zeros [shape]
  (let [[m n] (check shape)]
    (DoubleMatrix/zeros m n)))

(defn eye [n]
  (DoubleMatrix/eye n))

(defn diag [xs]
  (DoubleMatrix/diag (double-array xs)))

;;;; conversion

(defn clj-> [data]
  (assert (or (nil? data) (coll? data)))
  (let [shape (vec-shape data)
        size (reduce * shape)
        data (flatten data)]
    (assert (= (count data) size) "ragged arrays are not supported")
    (array (map double data) shape)))

(defn ->clj [mat]
  (let [n (.columns mat)]
    ;; we transpose, since jblas expects elements are in column-major order.
    (->> mat .transpose .elementsAsList (partition n))))

;;;; properties

(defn matrix? [mat]
  (instance? DoubleMatrix mat))

(defn shape [mat]
  [(.rows mat) (.columns mat)])

(defn size [mat]
  (.length mat))

(defn- array? [x] (instance? DoubleMatrix x))

(defn- prep [x] (if (array? x) x (double x)))

;;;; unary ops

(defn transpose [mat]
  (.transpose mat))

(defmacro uop [n matrix-op row-op column-op]
  `(defn ~n
     (~'[mat] (~matrix-op ~'mat))
     (~'[mat dim]
      (case ~'dim
        0 (~row-op ~'mat)
        1 (~column-op ~'mat)))))

(uop mean .mean .rowMeans .columnMeans)
(uop sum .sum .rowSums .columnSums)
(uop amin .min .rowMins .columnMins)
(uop amax .max .rowMaxs .columnMaxs)

(defn argmax
  ([mat]
   (let [i (.argmax mat)]
     [(.indexRows mat i) (.indexColumns mat i)]))
  ([mat dim]
   (let [xs (case dim
              0 (.rowArgmaxs mat)
              1 (.columnArgmaxs mat))]
     (into [] xs))))

(defn argmin
  ([mat]
   (let [i (.argmin mat)]
     [(.indexRows mat i) (.indexColumns mat i)]))
  ([mat dim]
   (let [xs (case dim
              0 (.rowArgmins mat)
              1 (.columnArgmins mat))]
     (into [] xs))))

(defn cumsum [mat]
  (.cumulativeSum mat))

(defn norm1 [mat]
  (.norm1 mat))

(defn norm2 [mat]
  (.norm2 mat))

(defn neg [mat]
  (.neg mat))

(defn not2 [mat]
  (.not mat))

;;;; element-wise binary operations

(defmacro bop [n forward-op reverse-op fallback]
  `(defn ~n ~'[a b]
     (cond
       (array? ~'a) (~forward-op ~'a (if (array? ~'b) ~'b (double ~'b)))
       (array? ~'b) (~reverse-op ~'b (double ~'a))
       :else (~fallback ~'a ~'b))))

(defmacro scalar-xor [a b]
  `(let [a# ~a b# ~b]
     (or (and a# (not b#)) (and (not a#) b#))))

(bop add .add .add +)
(bop sub .sub .rsub -)
(bop mul .mul .mul *)
(bop div .div .rdiv /)
(bop and2 .and .and clojure.core/and)
(bop or2 .or .or clojure.core/or)
(bop xor .xor .xor scalar-xor)
(bop min2 .min .min clojure.core/min)
(bop max2 .max .max clojure.core/max)

;;;; matrix-wise binary ops

(defn mmul [a b]
  (.mmul a b))

(defn dot [a b]
  (.dot a b))

;; element-wise comparisons

(bop eq .eq .eq =)
(bop lt .lt .gt <)
(bop gt .gt .lt >)
(bop le .le .ge <=)
(bop ge .ge .le >=)
