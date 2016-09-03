package audaque.com.cache.factory.test;

import java.io.FileWriter;
import java.io.IOException;

public class HashTest {

	static FileWriter writer = null;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		writer = new FileWriter("D:/hash.txt", true);

		int val = 0;
		for (int i = 0; i < 10000; i++) {
			val = i & 1000;
			writer.append(String.valueOf(val));
			writer.append(",");
			if (i % 10 == 0)
				writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	private static int hash(Object x) {
		int h = x.hashCode();

		// Spread bits to regularize both segment and index locations,
		// using variant of single-word Wang/Jenkins hash.
		h += (h << 16) ^ 0xffffcd7d;
		h ^= (h >>> 10);
		h += (h << 3);
		h ^= (h >>> 6);
		h += (h << 2) + (h << 14);

		return h ^ (h >>> 16);
	}
}
