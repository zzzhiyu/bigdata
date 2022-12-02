package src

import (
	"encoding/binary"
	"fmt"
	"strings"
)

const (
	DICT_OK              = 0
	DICT_ERR             = 1
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
	keyCompare   func(privaData interface{}, key1 interface{}, key2 interface{}) bool
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

// 是否启用rehash的标识
var dict_can_resize int = 1

// 强制rehash的比率
var dict_force_resize_ratio uint32 = 5

//typedef void (dictScanFunction)(void *privdata, const dictEntry *de);

func DICT_NOTUSED(v interface{}) interface{} {
	return v
}

func (d *dict) SetVal(entry *dicEntry, _vale_ interface{}) {
	if d.dType.valueDup != nil {
		entry.value = d.dType.valueDup(d.privDate, _vale_)
	} else {
		entry.value = _vale_
	}
}

func (d *dict) SetKey(entry *dicEntry, _key_ interface{}) {
	if d.dType.keyDup != nil {
		entry.key = d.dType.keyDup(d.privDate, _key_)
	} else {
		entry.key = _key_
	}
}

func (d *dict) CompareKeys(key1 interface{}, key2 interface{}) bool {
	if d.dType.keyCompare != nil {
		return d.dType.keyCompare(d.privDate, key1, key2)
	} else {
		return key1 == key2
	}
}

func (d *dict) IntHashFunction(key uint32) uint32 {
	key += ^(key << 15)
	key ^= (key >> 10)
	key += (key << 3)
	key ^= (key >> 6)
	key += ^(key << 11)
	key ^= (key >> 16)
	return key
}

func (dict *dict) IdentityHashFunction(key uint32) uint32 {
	return key
}

var dict_hash_function_seed uint32 = 5381

func (d *dict) SetHashFunctionSeed(seed uint32) {
	dict_hash_function_seed = seed
}

func (d *dict) GetHashFunctionSeed() uint32 {
	return dict_hash_function_seed
}

func (d *dict) GetHashFunction(key interface{}, len int) uint32 {
	data := []byte(fmt.Sprintf("%v", key.(interface{})))
	/* 'm' and 'r' are mixing constants generated offline.
	   They're not really 'magic', they just happen to work well.  */
	var seed uint32 = dict_hash_function_seed
	var m uint32 = 0x5bd1e995
	var r int = 24

	/* Initialize the hash to a 'random' value */
	var h uint32 = seed ^ uint32(len)

	index := 0
	for len >= 4 {
		k := binary.LittleEndian.Uint32(data[index:4])

		k *= m
		k ^= k >> r
		k *= m

		h *= m
		h ^= k

		index += 4
		len -= 4
	}

	switch len {
	case 3: h ^= uint32(data[2]) << 16
	case 2: h ^= uint32(data[1]) << 8
	case 1: h ^= uint32(data[0]); h *= m
	}

	h ^= h >> 13;
	h *= m;
	h ^= h >> 15;

	return h
}

func (d *dict) GenCaseHashFunction(buf interface{}, len int) uint32  {
	hash := dict_hash_function_seed
	data := []byte(strings.ToLower(fmt.Sprintf("%v", buf.(interface{}))))

	index := 0
	for len > 0 {
		hash = ((hash << 5) + hash) + uint32(data[index])
		index++
		len--
	}
	return hash
}