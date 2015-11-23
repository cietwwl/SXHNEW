package com.joyveb.tlol.util;

public enum CalcMode {
	Add("+", ""), Mul("×", ""), PercentAdd("+", "%"), PercentSub("-", "%");

	private String pre;
	private String ext;

	private CalcMode(final String pre, final String ext) {
		this.pre = pre;
		this.ext = ext;
	}

	public String fixValueString(final double value) {
		return "    " + pre + (int)value + ext;
	}

	public double takeEffect(final double value, final double arg) {
		switch (this) {
		case Add:
			return value + arg;
		case Mul:
			return value * arg;
		case PercentAdd:
			return value * (1 + arg);
		case PercentSub:
			return value * (1 - arg);
		default:
			return 0;
		}
	}

	public double loseEffect(final double value, final double arg) {
		switch (this) {
		case Add:
			return value - arg;
		case Mul:
			return value / arg;
		case PercentAdd:
			return value / (1 + arg);
		case PercentSub:
			return value / (1 - arg);
		default:
			return 0;
		}
	}
}
