package bstmap;

public class Mytest {
        public static void main(String[] args) {
            BSTMap<Integer, String> map = new BSTMap<>();
            map.put(1, "one");
            map.printKeySet();
            map.put(2, "two");
            map.printKeySet();
            map.put(3, "three");
            map.printKeySet();
            System.out.println(map.containsKey(2));  // 应该输出 true
            System.out.println(map.containsKey(4));  // 应该输出 false
        }
}
