# orthant-search
Orthant search is "one code to rule them all" for many operations in multiobjective evolutionary algorithms.

[![Build Status](https://api.travis-ci.com/mbuzdalov/orthant-search.png)](http://travis-ci.com/mbuzdalov/orthant-search)

This repository accompanies the paper:

*Buzdalov M.* Generalized Offline Orthant Search: One Code for Many Problems in Multiobjective Optimization 
// Proceedings of Genetic and Evolutionary Computation. - 2018. - P. 593-600.

The following reductions to orthant search are implemented and tested:

* Domination count (the number of points which a given point dominates), used in SPEA and SPEA2.
* Domination rank (the number of points that dominate a given point), used in MOGA and VEGA.
* Non-dominated sorting (used in NSGA-II, NSGA-III and many other algorithms).
* A "buggy" version of non-dominated sorting that assigns increasing ranks to several identical solutions.
* The additive binary epsilon-indicator (used mainly in assessing the performance of multiobjective optimization algorithms).
* Initial fitness assignment for the IBEA algorithm (the version that uses the additive binary epsilon-indicator).
* NEW: the R2 indicator, including the very recent version with an arbitrary power (typically equal to the dimension) applied to the addends.

## Acknowledgments

The following contributors would like to acknowledge the support of this research by the [Russian Scientific Foundation](http://рнф.рф/en),
agreement [17-71-20178](http://рнф.рф/en/enprjcard/?rid=17-71-20178):

* [Maxim Buzdalov](https://github.com/mbuzdalov)
