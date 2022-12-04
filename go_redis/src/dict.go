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

type DicEntry struct {
	Key   interface{}
	Value interface{}
	Next  *DicEntry
}

type DictHt struct {
	Table    **DicEntry //哈希表数组
	Size     uint64     //哈希表大小
	SizeMask uint64     //哈希表大小掩码,用于计算索引值， 总是等于size-1
	Used     uint64     //已有节点数量
}

type DictType struct {
	HashFunction func(key interface{}) uint32
	KeyDup       func(privData interface{}, key interface{}) interface{}
	ValueDup     func(privaData interface{}, value interface{}) interface{}
	KeyCompare   func(privaData interface{}, key1 interface{}, key2 interface{}) bool
}

type Dict struct {
	DictType  *DictType
	PrivDate  interface{}
	Ht        [2]DictHt
	ReHashIdx int //rehash索引,rehash不在进行时，值为-1
	Iterators int
}

type DictIterator struct {
	Dict        *Dict
	Table       int       //正在被迭代的哈希表号码 0、1
	Index       int       //迭代器当前所指向的哈希表索引位置
	Safe        int       //标识这个迭代是否安全
	Entry       *DicEntry //当前迭代到节点的指针
	NextEntry   *DicEntry //当前迭代节点的下一个节点
	FingerPrint int64
}

// 是否启用rehash的标识
var dict_can_resize int = 1

// 强制rehash的比率
var dict_force_resize_ratio uint32 = 5

//typedef void (dictScanFunction)(void *privdata, const dictEntry *de);

func DICT_NOTUSED(v interface{}) interface{} {
	return v
}

// 设置value
func (d *Dict) SetVal(entry *DicEntry, _vale_ interface{}) {
	if d.DictType.ValueDup != nil {
		entry.Value = d.DictType.ValueDup(d.PrivDate, _vale_)
	} else {
		entry.Value = _vale_
	}
}

// 设置key
func (d *Dict) SetKey(entry *DicEntry, _key_ interface{}) {
	if d.DictType.KeyDup != nil {
		entry.Key = d.DictType.KeyDup(d.PrivDate, _key_)
	} else {
		entry.Key = _key_
	}
}

// 比较key
func (d *Dict) CompareKeys(key1 interface{}, key2 interface{}) bool {
	if d.DictType.KeyCompare != nil {
		return d.DictType.KeyCompare(d.PrivDate, key1, key2)
	} else {
		return key1 == key2
	}
}

// int hash
func (d *Dict) IntHashFunction(key uint32) uint32 {
	key += ^(key << 15)
	key ^= key >> 10
	key += key << 3
	key ^= key >> 6
	key += ^(key << 11)
	key ^= key >> 16
	return key
}

func (dict *Dict) IdentityHashFunction(key uint32) uint32 {
	return key
}

var dict_hash_function_seed uint32 = 5381

// 设置hash函数的基值
func (d *Dict) SetHashFunctionSeed(seed uint32) {
	dict_hash_function_seed = seed
}

// 获取hash函数的基值
func (d *Dict) GetHashFunctionSeed() uint32 {
	return dict_hash_function_seed
}

// 得到hash值
func (d *Dict) GenHashFunction(key interface{}, len int) uint32 {
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
	case 3:
		h ^= uint32(data[2]) << 16
	case 2:
		h ^= uint32(data[1]) << 8
	case 1:
		h ^= uint32(data[0])
		h *= m
	}

	h ^= h >> 13
	h *= m
	h ^= h >> 15

	return h
}

// 得到string 的hash值
func (d *Dict) GenCaseHashFunction(buf interface{}, len int) uint32 {
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

// 重新设置dictht
func (ht *DictHt) Reset() {

	ht.Table = nil
	ht.Size = 0
	ht.SizeMask = 0
	ht.Used = 0
}

// 初始化dict
func (d *Dict) Init(dType *DictType, privData interface{}) int {
	//设置特定的函数
	d.DictType = dType
	//设置私有的数据
	d.PrivDate = privData
	//设置两个哈希表
	d.Ht[0] = DictHt{}
	d.Ht[0].Reset()
	d.Ht[1] = DictHt{}
	d.Ht[1].Reset()
	//rehash
	d.ReHashIdx = -1
	//字典的安全迭代器数量
	d.Iterators = -1

	return DICT_OK
}

////缩小给定的字典
//func (d *Dict) Resize()  {
//	if (!dict_can_resize || IsRe)
//
//}
