(ns numbers.opt.nelder-mead-test
  (:require [midje.sweet :refer :all]
            [numbers.opt.nelder-mead :refer :all]))

(facts
 "about the rosenbrock function"
 (rosenbrock [1.3 0.7 0.8 1.9 1.2]) => 848.22
 (rosenbrock [1 1 1 1 1]) => 0)

(facts
 "about nelder-mead"
 (let [guess [1.3 0.7 0.8 1.9 1.2]
       result [0.9999890368509758
               0.9999730530256266
               0.9999421495385137
               0.9998870416663606
               0.9997747672740844]]
   (nelder-mead rosenbrock guess) => result))
