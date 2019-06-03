package studio.greeks.data.novel.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/19 21:57
 */
public class BloomFilter<E extends Serializable> implements Set<E>, Serializable {
    private AtomicInteger counter = new AtomicInteger(0);
    private boolean[] status = new boolean[0x1000];
    private int[] EMPTY = new int[0];
    private transient MessageDigest digest;

    public BloomFilter() {
    }


    public BloomFilter(MessageDigest digest) {
        this.digest = digest;
    }



    public int size() {
        return counter.get();
    }

    public boolean isEmpty() {
        return counter.get() == 0;
    }

    private int[] serial(Object o){
        if(o instanceof Serializable){
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
                oos.writeObject(o);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                oos.close();
                if(null != digest){
                    bytes = digest.digest(bytes);
                }
                int[] result = new int[bytes.length-1];
                for (int i = 0; i < bytes.length-1; i++) {
                    result[i] = ((bytes[i]>>4) & 0x0f)*0x10 + ((bytes[i+1]>>4) & 0x0f);
                }
                return result;
            }catch (IOException ie){
                ie.printStackTrace();
            }
        }
        return EMPTY;
    }

    public boolean contains(Object o) {
        if(o instanceof Serializable){
            for (int i : serial((E) o)) {
                if(!status[i]){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    public boolean add(E e) {
        if(add(e, status)){
            counter.incrementAndGet();
            return true;
        }
        return false;
    }
    private boolean add(E e, boolean[] temp){
        int[] is = serial(e);
        assert temp.length == status.length;
        for (int i : is) {
            temp[i] = true;
        }
        return is.length==0;
    }
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if(!contains(o))
                return false;
            return true;
        }
        return false;
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean[] temp = new boolean[status.length];
        for (E e : c) {
            if(!add(e, temp)){
                return false;
            }
        }
        for (int i = 0; i < temp.length; i++) {
            status[i] = temp[i];
        }
        counter.set(counter.get()+c.size());
        return true;
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        counter.set(0);
        for (int i = 0; i < status.length; i++) {
            status[i] = false;
        }
    }

    public Spliterator<E> spliterator() {
        throw new UnsupportedOperationException();
    }
}