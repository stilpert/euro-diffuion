package defusion;

import lombok.Data;

@Data
public class Coordinate {
    private final int x;
    private final int y;
    public Coordinate (int x, int y) {
        if(x > 10 || x < 0 || y > 10 || y < 0) {
            throw new IllegalArgumentException("Index out pf boundaries [0; 10]");
        }
        this.x = x;
        this.y = y;
    }
}
