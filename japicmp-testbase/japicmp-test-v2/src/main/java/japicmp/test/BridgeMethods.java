package japicmp.test;

public class BridgeMethods {
	public class Node<T> {
		public T data;

		public Node(T data) {
			this.data = data;
		}

		public void setData(T data) {
			System.out.println("Node.setData");
			this.data = data;
		}

		public T getData() {
			return this.data;
		}
	}

	public class MyNode extends Node<Integer> {
		public MyNode(Integer data) {
			super(data);
		}

		public void setData(Integer data) {
			System.out.println("MyNode.setData");
			super.setData(data);
		}

		public Integer getData() {
			return super.getData();
		}
	}
}
