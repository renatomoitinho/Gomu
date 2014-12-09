package com.app.start.util;

/**
 * Created with IntelliJ IDEA.
 * 
 * @author renatomoitinhodias@gmail.com
 * @since 11/01/14 03:36
 * 
 *        use <code>robocop.filters.route.Tuples.of(objects...) </>
 * 
 */
public final class Tuples {

	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<>(a, b);
	}

	public static <A, B, C> Triplet<A, B, C> of(A a, B b, C c) {
		return new Triplet<>(a, b, c);
	}

	public static <A, B, C, D> Quartet<A, B, C, D> of(A a, B b, C c, D d) {
		return new Quartet<>(a, b, c, d);
	}

	private Tuples() {
		;
	} // no instance

	//
	public static class Pair<A, B> {

		public A first;
		public B second;

		private Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	}

	public static class Triplet<A, B, C> {

		public A first;
		public B second;
		public C third;

		private Triplet(A first, B second, C third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}
	}

	public static class Quartet<A, B, C, D> {

		public A first;
		public B second;
		public C third;
		public D fourth;

		private Quartet(A first, B second, C third, D fourth) {
			this.first = first;
			this.second = second;
			this.third = third;
			this.fourth = fourth;
		}
	}
}
