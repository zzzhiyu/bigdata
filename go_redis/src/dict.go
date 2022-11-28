package src

type dicEntry struct {
	key   interface{}
	value interface{}
	next  *dicEntry
}

type dictHt struct {
	table    **dicEntry //哈希表数组
	size     uint64     //哈希表大小
	sizeMask uint64     //哈希表大小掩码,用于计算索引值， 总是等于size-1
	used     uint64     //已有节点数量
}

type dictType struct {
	hashFunction func(key interface{}) uint32
	keyDup       func(privData interface{}, key interface{}) interface{}
}

type dict struct {
	privDate   interface{}
	dictHts    [2]dictHt
	treHashIdx int //rehash索引,rehash不在进行时，值为-1
}
