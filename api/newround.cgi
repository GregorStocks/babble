#!/usr/bin/env python
from __future__ import absolute_import

import lib.updateround as updateround
import lib.SQL as SQL

updateround.start_new_round(SQL.get_conn())
template.output_json(["IT'S ALL GOOD"])
