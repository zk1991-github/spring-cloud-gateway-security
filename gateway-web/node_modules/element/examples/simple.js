var element = require("../index")

element("<p>Hello</p><p>World</p>")
// => Hello World (as document fragment)

element("<p>Hello World</p>")
// => Hello World (as element)
