package com.xuecheng.framework;

/**
 * 测试异常传递的链条
 *
 * @author Walker_Don
 * @version V1.0
 * @ClassName TestExceptiion
 * @date 2019年07月12日 下午 4:33
 */

public class TestExceptiion2 {
	public String add() {
		return new TestExceptiion().add();
	}

	public static void main(String[] args) {
		String add = new TestExceptiion2().add();
		System.out.println(add);
	}
}
