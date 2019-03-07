public class methodFile {
	/**
	 * Get rid of all the digits that are even in the string.
	 * Examples of this function are:
	 * odds(323) = 33
	 * odds(13524) = 135
	 * odds(12345) = 135
	 */
	public static int odds(int x) {
		if (x == 0) return 0; 

		int res = odds(x / 10) * 10;
		
		if (x % 2 == 1) {
			res += x % 10;
		}
		return res;
	}
}