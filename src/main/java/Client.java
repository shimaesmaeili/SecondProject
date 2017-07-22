public class Client {
	public static void main(String[] args) throws Exception {
		Terminal terminal1 = new Terminal("1", "terminal.xml");
		terminal1.start();

		Terminal terminal2 = new Terminal("2", "terminal.xml");
		terminal2.start();
	}
}
