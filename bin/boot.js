#!/usr/bin/env node

const program = require("./volglass-cli.js")

process.argv.shift()
process.argv.shift()
program.volglass(process.argv)
