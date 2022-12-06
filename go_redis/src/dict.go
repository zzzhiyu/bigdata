package src

import (
	"encoding/binary"
	"fmt"
	"math"
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
	Table    []*DicEntry //哈希表数组
	Size     uint64      //哈希表大小
	SizeMask uint64      //哈希表大小掩码,用于计算索引值， 总是等于size-1
	Used     uint64      //已有节点数量
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

/* computer hash through given the key */
func (d *Dict) HashKey(key interface{}) uint32 {
	return d.DictType.HashFunction(key)
}

func (dh *DicEntry) GetKey() interface{} {
	return dh.Key
}

func (dh *DicEntry) GetValue() interface{} {
	return dh.Value
}

// 返回给定字典的大小
func (d *Dict) Slots() uint64 {
	return d.Ht[0].Size + d.Ht[1].Size
}

// 返回已有字典数量
func (d *Dict) Size() uint64 {
	return d.Ht[0].Used + d.Ht[1].Used
}

// 字典是否在rehash
func (d *Dict) IsRehashing() bool {
	return d.ReHashIdx > -1
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

// 初始化dict
func (d *Dict) Init(dType *DictType, privData interface{}) int {
	//设置特定的函数
	d.DictType = dType
	//设置私有的数据
	d.PrivDate = privData
	//设置两个哈希表
	d.Ht[0] = DictHt{Table: nil, Size: 0, SizeMask: 0, Used: 0}
	d.Ht[1] = DictHt{Table: nil, Size: 0, SizeMask: 0, Used: 0}
	//rehash
	d.ReHashIdx = -1
	//字典的安全迭代器数量
	d.Iterators = -1

	return DICT_OK
}

/*
resize the table to the minimal size the contains all the elements

	but with the invariant of UESD/BUCKETS ratio near to <= 1
*/
func (d *Dict) Resize() int {
	if dict_can_resize == 0 || d.IsRehashing() {
		return DICT_ERR
	}
	minimal := d.Ht[0].Used
	if minimal < DICT_HT_INITIAL_SIZE {
		minimal = DICT_HT_INITIAL_SIZE
	}
	return d.Expand(minimal)
}

/* Expand or create the hash table*/
func (d *Dict) Expand(size uint64) int {
	realSize := d.nextPower(size)
	if d.IsRehashing() || d.Ht[0].Used > size {
		return DICT_ERR
	}

	n := DictHt{
		Table:    make([]*DicEntry, realSize),
		Size:     realSize,
		SizeMask: realSize - 1,
		Used:     0}

	if d.Ht[0].Table == nil {
		d.Ht[0] = n
	} else {
		d.Ht[1] = n
		d.ReHashIdx = 0
	}
	return DICT_OK
}

/* Performs N steps of incremental rehashing. Returns 1 if there are still
 * keys to move from the old to the new hash table, otherwise 0 is returned.
 *
 * 执行 N 步渐进式 rehash 。
 *
 * 返回 1 表示仍有键需要从 0 号哈希表移动到 1 号哈希表，
 * 返回 0 则表示所有键都已经迁移完毕。
 *
 * Note that a rehashing step consists in moving a bucket (that may have more
 * than one key as we use chaining) from the old to the new hash table.
 *
 * 注意，每步 rehash 都是以一个哈希表索引（桶）作为单位的，
 * 一个桶里可能会有多个节点，
 * 被 rehash 的桶里的所有节点都会被移动到新哈希表。
 *
 * T = O(N)
 */
func (d *Dict) Rehash(n int) int {
	if !d.IsRehashing() {
		return 0
	}
	for ; n > 0; n-- {
		//0号hash为空，重新设置完成，ht[1]转到ht[0], ht[1]进行初始化
		if d.Ht[0].Used == 0 {
			d.Ht[0] = d.Ht[1]
			d.Ht[1] = DictHt{}
			d.ReHashIdx = -1
			return 0
		}

		if uint32(d.Ht[0].Size) > uint32(d.ReHashIdx) {
			panic(-1)
		}

		for d.Ht[0].Table[d.ReHashIdx] == nil {
			d.ReHashIdx++
		}

		de := d.Ht[0].Table[d.ReHashIdx]
		for de != nil {
			nextDe := de.Next
			// TODO

			de = nextDe
		}

	}
	return 1
}

/* ----------------------private Function---------------------*/

/*Expand the dict if needed*/
func (d *Dict) expandIfNeeded() int {
	if d.IsRehashing() {
		return DICT_OK
	}

	if d.Ht[0].Size == 0 {
		return d.Expand(DICT_HT_INITIAL_SIZE)
	}

	if d.Ht[0].Used >= d.Ht[0].Size && (dict_can_resize > 0 || d.Ht[0].Used/d.Ht[0].Size > uint64(dict_force_resize_ratio)) {
		return d.Expand(d.Ht[0].Used * 2)
	}

	return DICT_OK
}

/*Our hash table capability is a power of two*/
func (d *Dict) nextPower(size uint64) uint64 {
	i := uint64(DICT_HT_INITIAL_SIZE)

	if size >= math.MaxUint32 {
		return math.MaxUint32
	}

	for true {
		if i > size {
			break
		}
		i *= 2
	}
	return i
}

/* Returns the index of a free slot that can be populated with
 * a hash entry for the given 'key.
 * if the key already exists, -1 is returned
 * Note that if we are in the process of rehashing the hash table, the
 * index is always returned in the context of the second (new) hash table.
 * T = O(N)*/
func (d *Dict) keyIndex(key interface{}) int {
	var idx uint32
	if d.expandIfNeeded() == DICT_ERR {
		return -1
	}
	h := d.HashKey(key)
	for table := 0; table <= 1; table++ {
		//计算索引值
		idx = h & uint32(d.Ht[table].SizeMask)
		he := d.Ht[table].Table[idx]
		for he != nil {
			if d.CompareKeys(key, he.Key) {
				return -1
			}
			he = he.Next
		}

		if !d.IsRehashing() {
			break
		}
	}
	return int(idx)
}
