package bstmap;

public class Mytest {
        public static void main(String[] args) {
            BSTMap<Integer, String> map = new BSTMap<>();
            map.put(1, "one");
            map.put(2, "two");
            map.put(3, "three");
            System.out.println(map.containsKey(2));  // 应该输出 true
            System.out.println(map.containsKey(4));  // 应该输出 false
        }
}
