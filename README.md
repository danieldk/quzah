# Quzah

## Introduction

Quzah is a small Java library for generating RGB color sets for categorical data. The colors
are picked such that the distance in perception is maximized.

## FAQ

* *Can't I just randomly pick a set of colors from the RGB color space?*

  Not really. If you randomly pick two colors that are close to eachother, it will be hard to
  distinguish two categories.

* *So, then I just pick a set of colors distributed as uniformly as possible over the RGB color space?*

  That is certainly an improvement over picking colors at random. However, RGB is not a perceptually
  uniform color space. In other words, the Eucledian distance between two colors in RGB may not
  correspond to the distance in human perception.

* *Isn't it just easier and faster to use tables of visually distinct colors?*

  It is! In fact, generating the optimal set of colors may be too slow for your application. For this
  reason, Quzah includes static tables for different color set sizes.

That said, you may want to generate your own color sets. For instance, if you want to pick colors
from a subspace of the RGB color space. One example (also included in the library), is the
generation of pastel-like colors.

## Release plan

### 1.0.0

* Make Quzah available in the central repository.
