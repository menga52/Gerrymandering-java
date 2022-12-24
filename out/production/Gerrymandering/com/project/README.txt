Weights: a vector dictating the relative importance of each metric

Exponents:
positive coefficients:
*Voting outcome - never used. value has no effect
*compactness - increase value to increase penalty for more egregious offenses relative to minor offences.
        negative infinity = do not count (just use weight for this, though)

negative coefficients:
*punctures - same as compactness, mostly. e.g. 1 -> count total punctures, 2 -> one district with two punctures is worse than
        two districts with one each, 0 -> count punctured districts. values on [0,1] make the most sense.
*discontinuity - same as compactness. e.g. 0 -> return constant 0, 1 -> return (avg # components) - 1,
        2 -> penalize one district with three components more than 2 districts with 2