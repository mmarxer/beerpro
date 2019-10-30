package ch.beerpro.domain.utils;

public class BeerCart<T1, T2, T3, T4> {

    private final T1 sub1;
    private final T2 sub2;
    private final T3 sub3;
    private final T4 sub4;

    public BeerCart(T1 sub1, T2 sub2, T3 sub3, T4 sub4) {
        this.sub1 = sub1;
        this.sub2 = sub2;
        this.sub3 = sub3;
        this.sub4 = sub4;
    }

    public T1 getSub1() {
        return sub1;
    }
    public T2 getSub2() {
        return sub2;
    }
    public T3 getSub3() {
        return sub3;
    }
    public T4 getSub4() {
        return sub4;
    }
}