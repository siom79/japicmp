#!/usr/bin/env python3
"""Compares the japicmp result (result.csv) with the oracle of the corpus (compatibility.csv)."""
import collections, csv, sys

oracle = {r['change']: r for r in csv.DictReader(open(sys.argv[1]))}
result = {r['change']: r for r in csv.DictReader(open(sys.argv[2]), delimiter=';')}
LABEL = {('0', '0'): 'TP', ('1', '1'): 'TN', ('1', '0'): 'FP', ('0', '1'): 'FN'}
stats = collections.Counter()
for name in sorted(oracle):
    o, r = oracle[name], result[name]
    b = LABEL[(o['binary'], r['bin'])]
    s = LABEL[(o['source'], r['src'])]
    stats['binary ' + b] += 1
    stats['source ' + s] += 1
    if 'F' in b + s:
        print(f"{name}: oracle bin/src={o['binary']}/{o['source']} japicmp bin/src={r['bin']}/{r['src']} "
              f"[{b}/{s}] {r['types']}")
incompatible = [k for k in oracle if '0' in (oracle[k]['binary'], oracle[k]['source'])]
detected = sum('0' in (result[k]['bin'], result[k]['src']) for k in incompatible)
print(dict(stats))
print(f"incompatible scenarios detected: {detected}/{len(incompatible)} ({100.0 * detected / len(incompatible):.2f} %)")
