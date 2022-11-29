package src

const (
	DICT_OK  = 0
	DICT_ERR = 1
	DICT_HT_INITIAL_SIZE = 4
)

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
	valueDup     func(privaData interface{}, value interface{}) interface{}
	keyCompare   func(privaData interface{}, key1 interface{}, key2 interface{}) int
}

type dict struct {
	dType      *dictType
	privDate   interface{}
	dictHts    [2]dictHt
	treHashIdx int //rehash索引,rehash不在进行时，值为-1
}

type dictIterator struct {
	d           *dict
	table       int       //正在被迭代的哈希表号码 0、1
	index       int       //迭代器当前所指向的哈希表索引位置
	safe        int       //标识这个迭代是否安全
	entry       *dicEntry //当前迭代到节点的指针
	nextEntry   *dicEntry //当前迭代节点的下一个节点
	fingerPrint int64
}

func DICT_NOTUSED(v interface{}) interface{} {
	return v
}

func dictSetVal(d *dict, entry *dicEntry, _vale_ interface{})  {
	if d.dType.valueDup != nil {
		entry.value = d.dType.valueDup(d.privDate, _vale_)
	} else {
		entry.value = _vale_
	}
}

