package com.joyveb.tlol.auction;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * TreeSet的排序区间迭代器
 * @author Sid
 * 
 * @param <T> TreeSet模板参数
 */
public class TreeSetIntervalIterator<T> implements Iterator<T> {

	/**
	 * 原始的迭代器
	 */
	private Iterator<T> iterator;
	/**
	 * 迭代结束索引
	 */
	private int end;
	/**
	 * 当前迭代索引
	 */
	private int index = 1;
	
	/**
	 * @param treeSet 需要迭代的TreeSet
	 * @param start 迭代开始索引
	 * @param end 迭代结束索引
	 */
	public TreeSetIntervalIterator(final TreeSet<T> treeSet, final int start, final int end) {
		if(treeSet == null)
			throw new NullPointerException("参数treeSet不能为空");
		
		if(start < 1 || start > end)
			throw new IllegalArgumentException("迭代区间非法");
		
		this.iterator = treeSet.iterator();
		this.end = end;
		
		for(;index < start; index++)
			if(iterator.hasNext())
				iterator.next();
			else
				return;
	}
	
	@Override
	public boolean hasNext() {
		if(index > end)
			return false;
		
		return iterator.hasNext();
	}

	@Override
	public T next() {
		index++;
		return iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

}
