(ns numbers.opt.nelder-mead)

;; Minimize an n-dimensional scalar function using the downhill simplex method
;; of Nelder & Mead (1965).
;;
;; This implementation is meant for learning, and is highly inefficient.
;;
;; See also:
;;
;; - Nelder, J.A. and Mead, R. (1965), "A simplex method for function
;; minimization", Computer Journal.
;; - Gao & Han (2010). "Implementing the Nelder-Mead simplex algorithm with
;; adaptive parameters", Computational Optimization and Applications.

(def ALPHA 1.0)
(def BETA 0.5)
(def GAMMA 2.0)

(defn- centroid
  "Component-wise average of a seq of points."
  [points]
  (let [n (count points)
        sums (apply map + points)]
    (map #(/ % n) sums)))

(defn- weighted-sum
  "Component-wise weighted sum of two points."
  [weight1 point1 point2]
  (let [weight2 (- 1 weight1)
        f (fn [p1 p2]
            (+ (* weight1 p1) (* weight2 p2)))]
    (map f point1 point2)))

;; At each iteration, we have a set of n+1 distinct candidate points. Let p0 be
;; the worst point--i.e., the point with highest function value, and P be the
;; hyperplane that connects the remaining n points. Points are replaced
;; according to the following algorithm:
;; 1) reflect p0 about P and move away from P, if this is a new minimum point.
;; 2) just reflect p0 about P: if this is an improvement over at least one other
;; point in P.
;; 3) move p0 towards P (without reflection): if this improves p0.
;; 4) fallback strategy, since mucking with p0 didn't work: take all but the
;; best point p* and move them toward p*.

(defn- move-worst [f worst next-worst best but-worst]
  (let [center (centroid but-worst)
        reflect (weighted-sum (- ALPHA) worst center)
        f0 (f reflect)]
    (if (<= f0 (f best))
      (let [extend+reflect (weighted-sum GAMMA reflect center)]
        (if (< (f extend+reflect) f0)
          extend+reflect
          reflect))
      (if (< f0 (f next-worst))
        reflect
        (let [contract (weighted-sum BETA worst center)]
          (when (< (f contract) f0)
            contract))))))

(defn- move-non-best [best others]
  (let [adjusted (map #(weighted-sum 0.5 best %) others)]
    (conj adjusted best)))

(defn step [f points]
  (let [n (count points)
        points (sort-by f points)
        [best & middle] (take (- n 2) points)
        [next-worst worst] (take-last 2 points)
        but-worst (conj middle next-worst best)]
    (if-let [new-point (move-worst f worst next-worst best but-worst)]
      (conj but-worst new-point)
      (move-non-best best (conj middle worst next-worst)))))

(defn done? [f tol-f tol-x max-iter iter state]
  (let [state (map (juxt f identity) state)
        [best-f best] (first state)
        [worst-f worst] (last state)
        diff-f (Math/abs (- worst-f best-f))
        ;; this is an approximation, since we only compare best to worst.
        diff-x (apply max (map #(Math/abs (- %1 %2)) worst best))]
    (cond
      (<= max-iter iter) [:fail :too-many-iterations]
      (or (< diff-f tol-f) (< diff-x tol-x)) [:success best])))

(defn expand-guess
  "Initialize simplex using heuristic of Gao & Han."
  [x0]
  (let [n (count x0)
        f (fn [i x] (assoc x0 i (if (zero? x) 2.5e-4 (+ x 0.05))))]
    (conj (map-indexed f x0) x0)))

(defn nelder-mead [f initial-guess & {:keys [max-iter tol-f tol-x]
                                      :or {max-iter 1e6 tol-f 1e-8 tol-x 1e-8}}]
  (let [state0 (expand-guess initial-guess)
        states (iterate (partial step f) state0)
        done-indexed (fn [iter state]
                       (done? f tol-f tol-x max-iter iter state))
        [status data] (->> states
                           (map-indexed done-indexed)
                           (drop-while nil?)
                           first)]
    (when (not= status :success)
      (throw (Exception. (str "failed to minimize function: " data))))
    data))

(defn rosenbrock
  "Rosenbrock function, which is useful for testing direct optimization methods.
  Has a minimum at 0, when all x_i=1."
  [xs]
  (->> xs
       (partition 2 1)
       (map (fn [[x0 x1]]
              (let [a (- x1 (* x0 x0))
                    b (- 1 x0)]
                (+ (* a a 100)
                   (* b b)))))
       (reduce +)))
