package main

import "fmt"

type demo struct {
	aaa string
	bbb string
}

func main() {
	key := demo{
		aaa: "cccc",
		bbb: "dddd",
	}
	var value interface{}
	value = key
	data := []byte(fmt.Sprintf("%v", value.(interface{})))
	for _, v := range data {
		print(v)
	}
	println()
	key2 := demo{
		aaa: "cccc",
		bbb: "dddd",
	}

	value = key2
	data = []byte(fmt.Sprintf("%v", value.(interface{})))
	for _, v := range data {
		print(v)
	}
}
