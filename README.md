# Bet-and-Run Strategies for Optimization

[<img alt="Travis CI Build Status" src="https://img.shields.io/travis/thomasWeise/betAndRun/master.svg" height="20"/>](https://travis-ci.org/thomasWeise/betAndRun/)

## Introduction

In this repository, we provide the implementation of an improved generic Bet-and-Run strategy for black-box optimization.
The goal is to obtain the best possible results within a given time budget `T` using a given black-box optimization algorithm.
If no prior knowledge about problem features and algorithm behavior is available, the question about how to use the time budget most efficiently arises. We propose to first start `n>=1` independent runs of the algorithm during an initialization budget `T1<T`, pausing these runs, then apply a decision maker `D` to choose `1<=m<n` runs from them (consuming `T2>=0` time units in doing so), and then continuing these runs for the remaining `T3=T-T1-T2` time units.
In previous bet-and-run strategies, the decision maker `currentBest` would simply select the run with the best-so-far results at negligible time.
We propose using more advanced methods and test several different approaches, including neural networks trained or polynomials fitted on the current trace of the algorithm to predict which run may yield the best results if granted the remaining budget.
Applying this implementation to run "virtual experiments," one can find that this approach can yield better results than the previous methods, but also find that the `currentBest` method is a very reliable and robust baseline approach.


## Copyright

This repository is under MIT License, see file LICENSE.

The code on bet-and-run (mainly under cn.edu.hfuu.iao.betAndRun) is jointly developed by Dr. Thomas Weise ([http://iao.hfuu.edu.cn](http://iao.hfuu.edu.cn), [tweise@hfuu.edu.cn](mailto:tweise@hfuu.edu.cn), [tweise@gmx.de](mailto:tweise@gmx.de)) and Dr. Markus Wagner ([http://cs.adelaide.edu.au/~markus/](http://cs.adelaide.edu.au/~markus/), [markus.wagner@adelaide.edu.au](mailto:markus.wagner@adelaide.edu.au)).

The code for Artificial Neural Networks, Linear Algebra, and Evolution Strategies (e.g., CMA-ES) has originally been developed by Dr. Alexandre Devert ([http://www.marmakoide.org](http://www.marmakoide.org), [marmakoide@hotmail.fr](mailto:marmakoide@hotmail.fr), and [http://github.com/marmakoide](http://github.com/marmakoide)), who kindly granted us the permission to include it in our repository. The code published here is a slightly modified version of his code, but the copyright and authorship remains entirely with Dr. Devert, who provides it under the MIT license. He will probably eventually publish a canonical and maintained version of this code in a repository on GitHub under [http://github.com/marmakoide](http://github.com/marmakoide). Please contact Dr. Devert for any questions, in particular regarding licensing and (re-)distribution.