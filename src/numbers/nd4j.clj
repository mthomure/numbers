(ns numbers.nd4j
  (:refer-clojure :exclude [rand min max])
  (:import [org.nd4j.linalg.factory Nd4j]
           [org.nd4j.linalg.api.buffer DataBuffer]))

;; NOTE: the minimum size of an ndarray is n >= 2!

;; NOTE: shapes get dorked for rank < 2. we can't distinguish between shapes
;; of [2], [1 2], and [2 1].

;;;; creation

(defn array
  "Create array from 1D data and shape."
  [data shape]
  (Nd4j/create (float-array data) (int-array shape)))

(defmacro shaped-op [op]
  (let [sym (symbol (str "Nd4j/" op))]
    `(defn ~op ~'[shape] (~sym (int-array ~'shape)))))

(shaped-op ones)
(shaped-op rand)
(shaped-op zeros)

(defn eye [n]
  (Nd4j/eye n))

;;;; properties

(defn ndim [mat] (.rank mat))

(defn shape [mat] (into [] (.shape mat)))

(defn size [mat]
  (let [s (.shape mat)]
    (areduce s i ret (int 1) (* ret (aget s i)))))

(defn atype
  "Type of array (not type of element).

  scalar is 1x1 array
  row vector is 1xK array
  column vector is Kx1 array
  matrix is MxN array with M > 1 and N > 1
  tensor is array with ndim > 2"
  [mat]
  (let [s (shape mat)
        [n m] s]
    (cond
      (> (count s) 2) :tensor
      (= n m 1) :scalar
      (= n 1) :row
      (= m 1) :column
      :else :matrix)))

;;;; conversions

(defn vec-shape [data]
  (->> data (into []) (iterate first) (take-while coll?) (mapv count)))

(defn vec-> [data]
  (assert (or (nil? data) (coll? data)))
  (let [shape (vec-shape data)
        size (reduce * shape)
        data (flatten data)]
    (assert (= (count data) size) "ragged arrays are not supported")
    (if (zero? size)
      (Nd4j/create (float-array []))
      ;; WARNING: loss of precision! can't handle double-precision floats.
      (Nd4j/create (float-array size (map float data)) (int-array shape)))))

(defmulti ->vec atype)

(defmethod ->vec :scalar [mat]
  [[(.getFloat mat 0)]])

(defmethod ->vec :column [mat]
  (let [[n _] (shape mat)]
    (into [] (for [i (range n)] [(.getFloat mat i)]))))

(defmethod ->vec :row [mat]
  (let [[_ m] (shape mat)]
    [(into [] (for [i (range m)] (.getFloat mat i)))]))

(defmethod ->vec :matrix [mat]
  (let [[m n] (shape mat)]
    (for [i (range m)]
      (for [j (range n)]
        (.getFloat mat i j)))))

(defmethod ->vec :tensor [mat]
  (for [i (range (.slices mat))]
    (->vec (.slice mat i))))

;;;; modification

(defn transpose [mat]
  (.transpose mat))

(defn reshape [mat shape]
  (.reshape mat (int-array shape)))

;;;; unary operators

(defmacro unary-op [op]
  (let [full-op (symbol (str "Nd4j/" op))]
    `(defn ~op
       (~'[mat] (~full-op ~'mat))
       (~'[mat dim] (~full-op ~'mat ~'dim)))))

(unary-op cumsum)
(unary-op max)
(unary-op mean)
(unary-op min)
(unary-op norm1)
(unary-op norm2)
(unary-op normmax)
(unary-op prod)
(unary-op std)
(unary-op sum)
(unary-op var)
