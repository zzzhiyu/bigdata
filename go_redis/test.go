package main

func DictIntHashFunction(key uint32) uint32 {
	key += ^(key << 15)
	key ^= (key >> 10)
	key += (key << 3)
	key ^= (key >> 6)
	key += ^(key << 11)
	key ^= (key >> 16)
	return key
}

func main() {
	//var a uint32 = 1
	//fmt.Printf("%b", ^a)
	//fmt.Println()
	//fmt.Printf("%b", a)
	//var temp interface{}

	//word := "AAA"
	//temp = word
	//byteKeys := []byte(fmt.Sprintf("%v", temp.(interface{})))
	//for _, byteKey := range byteKeys {
	//	println(byteKey)
	//}

}
