package src

const (
	// 从表头向表尾进行迭代
	AL_START_HEAD = 0
	// 从表尾到表头进行迭代
	AL_START_TAIL = 1
)

// 双端链表节点
type listNode struct {
	pre   *listNode
	next  *listNode
	value interface{}
}

// 双端链表迭代器
type listIter struct {
	next      *listNode
	direction int
}

// 双端链表结构
type List struct {
	head  *listNode
	tail  *listNode
	dup   func(ptr interface{}) interface{}
	match func(ptr interface{}, key interface{}) int
	len   uint64
}

// 基本元素的获取
func (list *List) Length() uint64 {
	return list.len
}

func (list *List) First() *listNode {
	return list.head
}

func (list *List) Last() *listNode {
	return list.tail
}

func (listNode *listNode) PreNode() *listNode {
	return listNode.pre
}

func (listNode *listNode) NextNode() *listNode {
	return listNode.next
}

func (listNode *listNode) NodeValue() interface{} {
	return listNode.value
}

func (list *List) SetDupMethod(m func(ptr interface{}) interface{}) {
	list.dup = m
}

func (list *List) SetMatchMethod(m func(ptr interface{}, key interface{}) int) {
	list.match = m
}

func (list *List) GetDupMethod() func(ptr interface{}) interface{} {
	return list.dup
}

func (list *List) GetMatchMethod() func(ptr interface{}, key interface{}) int {
	return list.match
}

// 给链表表头增加新节点
func (list *List) AddNodeHead(value interface{}) {
	node := new(listNode)
	node.value = value

	if list.len == 0 {
		list.head = node
		list.tail = node
		node.next = nil
		node.pre = nil
	} else {
		node.pre = nil
		node.next = list.head
		list.head.pre = node
		list.head = node
	}
	list.len++
}

// 给链表表尾增加新节点
func (list *List) AddNodeTail(value interface{}) {
	node := new(listNode)
	node.value = value
	if list.len == 0 {
		list.head = node
		list.tail = node
		node.next = nil
		node.pre = nil
	} else {
		node.next = nil
		node.pre = list.tail
		list.tail.next = node
		list.tail = node
	}
	list.len++
}

// 在链表中插入数据
// 0:向前插入节点
// 1:向后插入节点
func (list *List) InsertNode(old_node *listNode, value interface{}, after int) {
	node := new(listNode)
	node.value = value
	if after == 1 {
		node.pre = old_node
		node.next = old_node.next
		if list.tail == old_node {
			list.tail = node
		}
	} else {
		node.next = old_node
		node.pre = old_node.pre
		if list.head == old_node {
			list.head = node
		}
	}
	if node.pre != nil {
		node.pre.next = node
	}
	if node.next != nil {
		node.next.pre = node
	}
	list.len++
}

func (list *List) DelNode(node *listNode) {
	if node.pre != nil {
		node.pre = node.next
	} else {
		list.head = node.next
	}

	if node.next != nil {
		node.next = node.pre
	} else {
		list.tail = node.pre
	}

	//情况node的值
	node.next = nil
	node.pre = nil
	node.value = nil

	list.len--
}

func (list *List) GetIterator(direction int) *listIter {
	iter := new(listIter)

	if direction == AL_START_HEAD {
		iter.next = list.head
	} else {
		iter.next = list.tail
	}

	iter.direction = direction
	return iter
}

func (li *listIter) RewindHead(list *List) {
	li.next = list.head
	li.direction = AL_START_HEAD
}

func (li *listIter) RewindTail(list *List) {
	li.next = list.tail
	li.direction = AL_START_TAIL
}

func (iter *listIter) Next() *listNode {
	current := iter.next

	if current != nil {
		if iter.direction == AL_START_HEAD {
			iter.next = current.next
		} else {
			iter.next = current.pre
		}
	}

	return current
}

func (list *List) Dup() *List {
	//初始化
	copyList := new(List)
	copyList.dup = list.dup
	copyList.match = list.match
	//获取iter
	iter := list.GetIterator(AL_START_HEAD)
	//进行遍历
	for node := iter.Next(); node != nil; node = iter.Next() {
		var value interface{}
		if copyList.dup != nil {
			value = copyList.dup(node.value)
			if value == nil {
				return nil
			}
		} else {
			value = node.value
		}
		//添加节点
		copyList.AddNodeTail(value)
	}

	return copyList
}

func (list *List) SearchKey(key interface{}) *listNode {
	iter := list.GetIterator(AL_START_HEAD)
	for node := iter.Next(); node != nil; node = iter.Next() {
		if list.match != nil {
			if list.match(node.value, key) > 0 {
				return node
			}
		} else {
			if node.value == key {
				return node
			}
		}
	}
	return nil
}

/*
 * 返回链表在给定索引上的值。
 *
 * 索引以 0 为起始，也可以是负数， -1 表示链表最后一个节点，诸如此类。
 *
 * 如果索引超出范围（out of range），返回 NULL 。
 *
 * T = O(N)
 */
func (list *List) Index(index int64) *listNode {
	var node *listNode
	if index < 0 {
		index = (-index) - 1
		node = list.tail
		for index > 0 && node != nil {
			node = node.pre
			index--
		}
	} else {
		node = list.head
		for index > 0 && node != nil {
			node = node.next
			index--
		}
	}

	return node
}

/*
取出链表的表尾节点，并将它移动到表头，成为新的表头节点。
*/
func (list *List) Rotate() {
	tail := list.tail
	if list.len <= 1 {
		return
	}
	//取出表尾元素
	list.tail = tail.pre
	list.tail.next = nil
	//插入到表头
	list.head.pre = tail
	tail.pre = nil
	tail.next = list.head
	list.head = tail
}
