package com.xuecheng.framework;

/**
 * 测试异常传递的链条,往上抛异常
 *
 * @author Walker_Don
 * @version V1.0
 * @ClassName TestExceptiion
 * @date 2019年07月12日 下午 4:33
 */

public class TestExceptiion {
	public String add() {
		return 12 / 0 + "";
	}

	public static void main(String[] args) {
		String add = new TestExceptiion().add();
		System.out.println(add);
	}
}
