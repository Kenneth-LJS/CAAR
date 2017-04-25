package audio;

public interface IAudio<E> {

    public int available();
    public void close();
    public E read();
    public int read(E[] b);
    public int read(E[] b, int off, int len);
    public long skip(long n);
    
}
