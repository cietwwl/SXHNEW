package com.joyveb.tlol.item;

import java.util.StringTokenizer;

import com.joyveb.tlol.role.Property;
import com.joyveb.tlol.util.CalcMode;

/**
 * 属性加成
 */
public class Bonus {
	/**
	 * 作用属性
	 */
	private Property effectProperty;
	/**
	 * 计算方式
	 */
	private CalcMode calcMode;
	/**
	 * 计算参数
	 */
	private double arg;

	/**
	 * 构造函数
	 */
	public Bonus() { }
	
	/**
	 * 构造函数
	 * @param effectProperty 作用属性
	 * @param calcMode 计算方式
	 * @param arg 计算参数
	 */
	public Bonus(final Property effectProperty, final CalcMode calcMode, final double arg) {
		this.effectProperty = effectProperty;
		this.calcMode = calcMode;
		this.arg = arg;
	}

	/**
	 * 计算加成效果
	 * @param value 初始值
	 * @return 最终值
	 */
	public double takeEffect(final double value) {
		return calcMode.takeEffect(value, arg);
	}

	/**
	 * 去掉加成效果
	 * @param value 初始值
	 * @return 最终值
	 */
	public double loseEffect(final double value) {
		return calcMode.loseEffect(value, arg);
	}

	@Override
	public String toString() {
		return effectProperty + calcMode.fixValueString(arg);
	}

	/**
	 * @return 作用属性
	 */
	public Property getEffectProperty() {
		return effectProperty;
	}

	/**
	 * @return 计算方式
	 */
	public CalcMode getCalcMode() {
		return calcMode;
	}

	/**
	 * @return 计算参数
	 */
	public double getArg() {
		return arg;
	}
	
	/**
	 * @param arg 计算参数
	 */
	public void setArg(final double arg) {
		this.arg = arg;
	}

	/**
	 * 序列化
	 * @return 序列化字符串
	 */
	public String serialize() {
		return effectProperty.name() + ";" + calcMode.name() + ";" + arg;
	}
	
	/**
	 * 反序列化
	 * @param tokenizer 数据源
	 * @return Bonus
	 */
	public static Bonus readBonus(final StringTokenizer tokenizer) {
		Bonus bonus = new Bonus();
		bonus.effectProperty = Property.valueOf(tokenizer.nextToken()); //value
		tokenizer.nextToken();
		bonus.calcMode = CalcMode.valueOf(tokenizer.nextToken()); //value
		tokenizer.nextToken();
		bonus.arg = Double.parseDouble(tokenizer.nextToken()); //value
		
		tokenizer.nextToken(); //)
		
		return bonus;
	}
	
}
