(ns numbers.util-test
  (:require [midje.sweet :refer :all]
            [numbers.util :refer :all]))

(facts
 (vec-shape nil) => [0]
 (vec-shape []) => [0]
 (vec-shape [[]]) => [1 0]
 (vec-shape [[[1 2 3] [4 5 6]]]) => [1 2 3]
 (vec-shape [[[1 2 3]] [[4 5 6]]]) => [2 1 3])
